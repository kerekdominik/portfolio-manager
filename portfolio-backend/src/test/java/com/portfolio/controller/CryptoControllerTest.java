package com.portfolio.controller;

import com.portfolio.dto.CryptoRequestDto;
import com.portfolio.dto.CryptoResponseDto;
import com.portfolio.dto.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.entity.Group;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.User;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.Stock;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.PortfolioAssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.ExternalCryptoService;
import com.portfolio.service.impl.CryptoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 1, 15);

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private CryptoListRepository cryptoListRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private CryptoService cryptoService;
    @Mock
    private ExternalCryptoService externalCryptoService;

    private CryptoController controller;
    private User user;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        controller = new CryptoController(portfolioAssetRepository, groupRepository, cryptoListRepository,
                portfolioRepository, cryptoService, externalCryptoService, clock);

        user = User.builder().id(1L).email("test@example.com").build();
        portfolio = new Portfolio();
        portfolio.setId(10L);
        portfolio.setUser(user);
    }

    @AfterEach
    void clearInterruptFlag() {
        Thread.interrupted();
    }

    private static CurrentCryptoResponse priceResponse(String id, double usd) {
        return new CurrentCryptoResponse(Map.of(id, Map.of("usd", usd)));
    }

    @Test
    void addCrypto_savesAssetWithCurrentPriceTodayAndGroup() throws Exception {
        Group group = new Group("Long term", user);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(cryptoListRepository.findById("bitcoin")).thenReturn(Optional.of(new CryptoListItem("bitcoin", "btc", "Bitcoin")));
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenReturn(priceResponse("bitcoin", 50000.0));
        when(groupRepository.findById(5L)).thenReturn(Optional.of(group));

        ResponseEntity<Map<String, String>> response = controller.addCryptoToPortfolio(
                new CryptoRequestDto("bitcoin", 40000.0, 0.5, null, 5L), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Crypto added to portfolio successfully", response.getBody().get("message"));

        ArgumentCaptor<PortfolioAsset> saved = ArgumentCaptor.forClass(PortfolioAsset.class);
        verify(portfolioAssetRepository).save(saved.capture());
        PortfolioAsset asset = saved.getValue();
        assertEquals(portfolio, asset.getPortfolio());
        assertEquals(40000.0, asset.getPriceWhenBought());
        assertEquals(0.5, asset.getQuantity());
        assertEquals(TODAY, asset.getPurchaseDate());
        assertEquals(50000.0, asset.getCurrentPrice());
        assertEquals(group, asset.getGroup());
        assertEquals("bitcoin", ((Crypto) asset.getAsset()).getExternalId());
        verify(cryptoService).saveCrypto(any(Crypto.class));
    }

    @Test
    void addCrypto_keepsGivenPurchaseDateAndSkipsGroupWhenNoneGiven() throws Exception {
        LocalDate purchaseDate = LocalDate.of(2025, 6, 1);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(cryptoListRepository.findById("ethereum")).thenReturn(Optional.of(new CryptoListItem("ethereum", "eth", "Ethereum")));
        when(externalCryptoService.getCryptoPriceInUsd("ethereum")).thenReturn(priceResponse("ethereum", 3000.0));

        controller.addCryptoToPortfolio(new CryptoRequestDto("ethereum", 2500.0, 1.0, purchaseDate, null), user);

        ArgumentCaptor<PortfolioAsset> saved = ArgumentCaptor.forClass(PortfolioAsset.class);
        verify(portfolioAssetRepository).save(saved.capture());
        assertEquals(purchaseDate, saved.getValue().getPurchaseDate());
        assertNull(saved.getValue().getGroup());
        verifyNoInteractions(groupRepository);
    }

    @Test
    void addCrypto_unknownId_returnsBadRequest() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(cryptoListRepository.findById("unknown")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.addCryptoToPortfolio(
                new CryptoRequestDto("unknown", 1.0, 1.0, null, null), user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid asset ID", response.getBody().get("error"));
        verifyNoInteractions(portfolioAssetRepository, cryptoService);
    }

    @Test
    void addCrypto_withoutPortfolio_throws() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.empty());
        CryptoRequestDto request = new CryptoRequestDto("bitcoin", 1.0, 1.0, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.addCryptoToPortfolio(request, user));
        assertEquals("Portfolio not found for user", exception.getMessage());
    }

    @Test
    void addCrypto_priceLookupFails_throwsExternalApiException() throws Exception {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(cryptoListRepository.findById("bitcoin")).thenReturn(Optional.of(new CryptoListItem("bitcoin", "btc", "Bitcoin")));
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenThrow(new IOException("CoinGecko down"));
        CryptoRequestDto request = new CryptoRequestDto("bitcoin", 1.0, 1.0, null, null);

        ExternalApiException exception = assertThrows(ExternalApiException.class,
                () -> controller.addCryptoToPortfolio(request, user));
        assertInstanceOf(IOException.class, exception.getCause());
        verify(portfolioAssetRepository, never()).save(any());
    }

    @Test
    void addCrypto_priceLookupInterrupted_keepsInterruptFlag() throws Exception {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(cryptoListRepository.findById("bitcoin")).thenReturn(Optional.of(new CryptoListItem("bitcoin", "btc", "Bitcoin")));
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenThrow(new InterruptedException());
        CryptoRequestDto request = new CryptoRequestDto("bitcoin", 1.0, 1.0, null, null);

        assertThrows(ExternalApiException.class, () -> controller.addCryptoToPortfolio(request, user));
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void getAllCryptos_returnsOnlyCryptosWithFreshPrices() throws Exception {
        PortfolioAsset bitcoin = new PortfolioAsset(1L, portfolio, new Crypto("bitcoin", "Bitcoin", "btc"),
                new Group("Long term", user), 2.0, LocalDate.of(2025, 1, 1), 100.0, 0.0);
        PortfolioAsset apple = new PortfolioAsset(2L, portfolio, new Stock("AAPL", "Apple", "NASDAQ"),
                null, 1.0, LocalDate.of(2025, 1, 1), 150.0, 160.0);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(List.of(bitcoin, apple));
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenReturn(priceResponse("bitcoin", 150.0));

        ResponseEntity<List<CryptoResponseDto>> response = controller.getAllCryptosInPortfolio(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        CryptoResponseDto dto = response.getBody().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Bitcoin", dto.getName());
        assertEquals("btc", dto.getSymbol());
        assertEquals(200.0, dto.getOriginalValue());
        assertEquals(150.0, dto.getCurrentPrice());
        assertEquals(300.0, dto.getCurrentValue());
        assertEquals(100.0, dto.getPnl());
        assertEquals("Long term", dto.getGroupName());
    }

    @Test
    void updateCrypto_updatesFieldsAndClearsGroup() {
        PortfolioAsset existing = new PortfolioAsset();
        existing.setGroup(new Group("Old", user));
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(1L, portfolio)).thenReturn(Optional.of(existing));

        ResponseEntity<Map<String, String>> response = controller.updateCryptoInPortfolio(
                1L, new CryptoRequestDto("bitcoin", 200.0, 3.0, null, null), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200.0, existing.getPriceWhenBought());
        assertEquals(3.0, existing.getQuantity());
        assertEquals(TODAY, existing.getPurchaseDate());
        assertNull(existing.getGroup());
        verify(portfolioAssetRepository).save(existing);
    }

    @Test
    void updateCrypto_setsGroupWhenGiven() {
        PortfolioAsset existing = new PortfolioAsset();
        Group group = new Group("New", user);
        LocalDate purchaseDate = LocalDate.of(2025, 3, 3);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(1L, portfolio)).thenReturn(Optional.of(existing));
        when(groupRepository.findById(7L)).thenReturn(Optional.of(group));

        controller.updateCryptoInPortfolio(1L, new CryptoRequestDto("bitcoin", 1.0, 1.0, purchaseDate, 7L), user);

        assertEquals(group, existing.getGroup());
        assertEquals(purchaseDate, existing.getPurchaseDate());
    }

    @Test
    void updateCrypto_notFound_returns404() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(99L, portfolio)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.updateCryptoInPortfolio(
                99L, new CryptoRequestDto("bitcoin", 1.0, 1.0, null, null), user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Crypto asset not found", response.getBody().get("error"));
    }

    @Test
    void deleteCrypto_found_deletesAsset() {
        PortfolioAsset existing = new PortfolioAsset();
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(1L, portfolio)).thenReturn(Optional.of(existing));

        ResponseEntity<Map<String, String>> response = controller.deleteCryptoFromPortfolio(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(portfolioAssetRepository).delete(existing);
    }

    @Test
    void deleteCrypto_notFound_returns404() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(99L, portfolio)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.deleteCryptoFromPortfolio(99L, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(portfolioAssetRepository, never()).delete(any());
    }
}

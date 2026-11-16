package com.portfolio.controller;

import com.portfolio.dto.StockRequestDto;
import com.portfolio.dto.StockResponseDto;
import com.portfolio.entity.Group;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.User;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.Stock;
import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.PortfolioAssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.repository.StockListRepository;
import com.portfolio.service.ExternalStockService;
import com.portfolio.service.impl.StockService;
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
class StockControllerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 1, 15);

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private StockListRepository stockListRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private StockService stockService;
    @Mock
    private ExternalStockService externalStockService;

    private StockController controller;
    private User user;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        controller = new StockController(portfolioAssetRepository, groupRepository, stockListRepository,
                portfolioRepository, stockService, externalStockService, clock);

        user = User.builder().id(1L).email("test@example.com").build();
        portfolio = new Portfolio();
        portfolio.setId(10L);
        portfolio.setUser(user);
    }

    @AfterEach
    void clearInterruptFlag() {
        Thread.interrupted();
    }

    private static StockRequestDto request(String symbol, double price, double quantity, LocalDate date, Long groupId) {
        StockRequestDto dto = new StockRequestDto();
        dto.setSymbol(symbol);
        dto.setPrice(price);
        dto.setQuantity(quantity);
        dto.setPurchaseDate(date);
        dto.setGroupId(groupId);
        return dto;
    }

    private void givenListedStock(String symbol) {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(stockListRepository.findBySymbol(symbol))
                .thenReturn(Optional.of(new StockListItem(symbol, "Apple Inc.", "NASDAQ", "Stock", "Active")));
    }

    @Test
    void addStock_savesAssetWithCurrentPriceTodayAndGroup() throws Exception {
        Group group = new Group("Tech", user);
        givenListedStock("AAPL");
        when(externalStockService.getCurrentStockPriceInUSD("AAPL")).thenReturn(190.0);
        when(groupRepository.findById(5L)).thenReturn(Optional.of(group));

        ResponseEntity<Map<String, String>> response = controller.addStockToPortfolio(
                request("AAPL", 150.0, 10.0, null, 5L), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stock added to portfolio successfully", response.getBody().get("message"));

        ArgumentCaptor<PortfolioAsset> saved = ArgumentCaptor.forClass(PortfolioAsset.class);
        verify(portfolioAssetRepository).save(saved.capture());
        PortfolioAsset asset = saved.getValue();
        assertEquals(150.0, asset.getPriceWhenBought());
        assertEquals(10.0, asset.getQuantity());
        assertEquals(TODAY, asset.getPurchaseDate());
        assertEquals(190.0, asset.getCurrentPrice());
        assertEquals(group, asset.getGroup());
        Stock stock = (Stock) asset.getAsset();
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("NASDAQ", stock.getExchange());
        verify(stockService).saveStock(any(Stock.class));
    }

    @Test
    void addStock_keepsGivenPurchaseDateWithoutGroup() throws Exception {
        LocalDate purchaseDate = LocalDate.of(2025, 6, 1);
        givenListedStock("AAPL");
        when(externalStockService.getCurrentStockPriceInUSD("AAPL")).thenReturn(190.0);

        controller.addStockToPortfolio(request("AAPL", 150.0, 1.0, purchaseDate, null), user);

        ArgumentCaptor<PortfolioAsset> saved = ArgumentCaptor.forClass(PortfolioAsset.class);
        verify(portfolioAssetRepository).save(saved.capture());
        assertEquals(purchaseDate, saved.getValue().getPurchaseDate());
        verifyNoInteractions(groupRepository);
    }

    @Test
    void addStock_unknownSymbol_returnsBadRequest() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(stockListRepository.findBySymbol("XXXX")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.addStockToPortfolio(
                request("XXXX", 1.0, 1.0, null, null), user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid stock symbol", response.getBody().get("error"));
        verifyNoInteractions(portfolioAssetRepository, stockService);
    }

    @Test
    void addStock_withoutPortfolio_throws() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.empty());
        StockRequestDto request = request("AAPL", 1.0, 1.0, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.addStockToPortfolio(request, user));
        assertEquals("Portfolio not found for user", exception.getMessage());
    }

    @Test
    void addStock_priceLookupFails_throwsExternalApiException() throws Exception {
        givenListedStock("AAPL");
        when(externalStockService.getCurrentStockPriceInUSD("AAPL")).thenThrow(new IOException("Alpha Vantage down"));
        StockRequestDto request = request("AAPL", 1.0, 1.0, null, null);

        ExternalApiException exception = assertThrows(ExternalApiException.class,
                () -> controller.addStockToPortfolio(request, user));
        assertInstanceOf(IOException.class, exception.getCause());
        verify(portfolioAssetRepository, never()).save(any());
    }

    @Test
    void addStock_priceLookupInterrupted_keepsInterruptFlag() throws Exception {
        givenListedStock("AAPL");
        when(externalStockService.getCurrentStockPriceInUSD("AAPL")).thenThrow(new InterruptedException());
        StockRequestDto request = request("AAPL", 1.0, 1.0, null, null);

        assertThrows(ExternalApiException.class, () -> controller.addStockToPortfolio(request, user));
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void getAllStocks_returnsOnlyStocks() {
        PortfolioAsset apple = new PortfolioAsset(2L, portfolio, new Stock("AAPL", "Apple", "NASDAQ"),
                new Group("Tech", user), 2.0, LocalDate.of(2025, 1, 1), 150.0, 160.0);
        PortfolioAsset bitcoin = new PortfolioAsset(1L, portfolio, new Crypto("bitcoin", "Bitcoin", "btc"),
                null, 1.0, LocalDate.of(2025, 1, 1), 100.0, 120.0);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(List.of(apple, bitcoin));

        ResponseEntity<List<StockResponseDto>> response = controller.getAllStocksInPortfolio(user);

        assertEquals(1, response.getBody().size());
        StockResponseDto dto = response.getBody().get(0);
        assertEquals("AAPL", dto.getSymbol());
        assertEquals(300.0, dto.getOriginalValue());
        assertEquals(320.0, dto.getCurrentValue());
        assertEquals(20.0, dto.getPnl());
        assertEquals("Tech", dto.getGroupName());
    }

    @Test
    void getAllStocks_withoutGroup_hasNoGroupName() {
        PortfolioAsset apple = new PortfolioAsset(2L, portfolio, new Stock("AAPL", "Apple", "NASDAQ"),
                null, 1.0, LocalDate.of(2025, 1, 1), 150.0, 160.0);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(List.of(apple));

        StockResponseDto dto = controller.getAllStocksInPortfolio(user).getBody().get(0);

        assertNull(dto.getGroupName());
    }

    @Test
    void updateStock_updatesFieldsAndClearsGroup() {
        PortfolioAsset existing = new PortfolioAsset();
        existing.setGroup(new Group("Old", user));
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(1L, portfolio)).thenReturn(Optional.of(existing));

        ResponseEntity<Map<String, String>> response = controller.updateStockInPortfolio(
                1L, request("AAPL", 175.0, 4.0, null, null), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(175.0, existing.getPriceWhenBought());
        assertEquals(4.0, existing.getQuantity());
        assertEquals(TODAY, existing.getPurchaseDate());
        assertNull(existing.getGroup());
        verify(portfolioAssetRepository).save(existing);
    }

    @Test
    void updateStock_setsGroupWhenGiven() {
        PortfolioAsset existing = new PortfolioAsset();
        Group group = new Group("Tech", user);
        LocalDate purchaseDate = LocalDate.of(2025, 3, 3);
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(1L, portfolio)).thenReturn(Optional.of(existing));
        when(groupRepository.findById(7L)).thenReturn(Optional.of(group));

        controller.updateStockInPortfolio(1L, request("AAPL", 1.0, 1.0, purchaseDate, 7L), user);

        assertEquals(group, existing.getGroup());
        assertEquals(purchaseDate, existing.getPurchaseDate());
    }

    @Test
    void updateStock_notFound_returns404() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(99L, portfolio)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.updateStockInPortfolio(
                99L, request("AAPL", 1.0, 1.0, null, null), user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Stock asset not found", response.getBody().get("error"));
    }

    @Test
    void deleteStock_found_deletesAsset() {
        PortfolioAsset existing = new PortfolioAsset();
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(1L, portfolio)).thenReturn(Optional.of(existing));

        ResponseEntity<Map<String, String>> response = controller.deleteStockFromPortfolio(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(portfolioAssetRepository).delete(existing);
    }

    @Test
    void deleteStock_notFound_returns404() {
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
        when(portfolioAssetRepository.findByIdAndPortfolio(99L, portfolio)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = controller.deleteStockFromPortfolio(99L, user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(portfolioAssetRepository, never()).delete(any());
    }
}

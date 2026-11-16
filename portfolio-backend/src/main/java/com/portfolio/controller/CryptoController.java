package com.portfolio.controller;

import com.portfolio.dto.CryptoRequestDto;
import com.portfolio.dto.CryptoResponseDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.User;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.PortfolioAssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.impl.CryptoService;
import com.portfolio.service.ExternalCryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
@Tag(name = "CryptoController", description = "API for managing cryptocurrencies in portfolios")
public class CryptoController {

    private static final String PORTFOLIO_NOT_FOUND = "Portfolio not found for user";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String CRYPTO_NOT_FOUND = "Crypto asset not found";

    private final PortfolioAssetRepository portfolioAssetRepository;
    private final GroupRepository groupRepository;
    private final CryptoListRepository cryptoListRepository;
    private final PortfolioRepository portfolioRepository;
    private final CryptoService cryptoService;
    private final ExternalCryptoService externalCryptoService;
    private final Clock clock;

    @PostMapping
    @Operation(summary = "Add cryptocurrency to portfolio", description = "Adds a new cryptocurrency to the user's portfolio.")
    public ResponseEntity<Map<String, String>> addCryptoToPortfolio(
            @RequestBody CryptoRequestDto cryptoRequest,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(PORTFOLIO_NOT_FOUND));

        Optional<CryptoListItem> cryptoListItemOpt = cryptoListRepository.findById(cryptoRequest.getId());
        if (cryptoListItemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(ERROR, "Invalid asset ID"));
        }

        Crypto crypto = new Crypto(
                cryptoListItemOpt.get().getId(), // CoinGecko ID
                cryptoListItemOpt.get().getName(),
                cryptoListItemOpt.get().getSymbol()
        );
        crypto.setExternalId(cryptoRequest.getId());
        cryptoService.saveCrypto(crypto);

        PortfolioAsset portfolioAsset = new PortfolioAsset();
        portfolioAsset.setPortfolio(portfolio);
        portfolioAsset.setAsset(crypto);
        portfolioAsset.setPriceWhenBought(cryptoRequest.getPrice());
        portfolioAsset.setQuantity(cryptoRequest.getQuantity());
        portfolioAsset.setPurchaseDate(
                cryptoRequest.getPurchaseDate() != null ? cryptoRequest.getPurchaseDate() : LocalDate.now(clock)
        );

        portfolioAsset.setCurrentPrice(fetchCurrentPrice(cryptoRequest.getId()));

        if (cryptoRequest.getGroupId() != null) {
            groupRepository.findById(cryptoRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of(MESSAGE, "Crypto added to portfolio successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all cryptocurrencies in portfolio", description = "Retrieves all cryptocurrencies in the user's portfolio.")
    public ResponseEntity<List<CryptoResponseDto>> getAllCryptosInPortfolio(
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(PORTFOLIO_NOT_FOUND));

        List<PortfolioAsset> assets = portfolioAssetRepository.findByPortfolio(portfolio);
        List<CryptoResponseDto> cryptos = assets.stream()
                .filter(asset -> asset.getAsset() instanceof Crypto)
                .map(crypto -> {
                    Crypto temp = (Crypto) crypto.getAsset();
                    crypto.setCurrentPrice(fetchCurrentPrice(temp.getExternalId()));
                    return getCryptoResponseDto(crypto);
                })
                .toList();

        return ResponseEntity.ok(cryptos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cryptocurrency in portfolio", description = "Updates the details of a cryptocurrency in the user's portfolio.")
    public ResponseEntity<Map<String, String>> updateCryptoInPortfolio(
            @PathVariable Long id,
            @RequestBody CryptoRequestDto cryptoRequest,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(PORTFOLIO_NOT_FOUND));

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, CRYPTO_NOT_FOUND));
        }

        PortfolioAsset portfolioAsset = portfolioAssetOpt.get();
        portfolioAsset.setPriceWhenBought(cryptoRequest.getPrice());
        portfolioAsset.setQuantity(cryptoRequest.getQuantity());
        portfolioAsset.setPurchaseDate(
                cryptoRequest.getPurchaseDate() != null ? cryptoRequest.getPurchaseDate() : LocalDate.now(clock)
        );

        if (cryptoRequest.getGroupId() != null) {
            groupRepository.findById(cryptoRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        } else {
            portfolioAsset.setGroup(null);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of(MESSAGE, "Crypto updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cryptocurrency from portfolio", description = "Removes a cryptocurrency from the user's portfolio.")
    public ResponseEntity<Map<String, String>> deleteCryptoFromPortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException(PORTFOLIO_NOT_FOUND));

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isPresent()) {
            portfolioAssetRepository.delete(portfolioAssetOpt.get());
            return ResponseEntity.ok(Map.of(MESSAGE, "Crypto deleted successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, CRYPTO_NOT_FOUND));
    }

    private double fetchCurrentPrice(String externalId) {
        try {
            return externalCryptoService.getCryptoPriceInUsd(externalId).getPriceInUSD(externalId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException("Interrupted while fetching current price for crypto " + externalId, e);
        } catch (IOException e) {
            log.error("Error fetching current price for crypto {}", externalId, e);
            throw new ExternalApiException("Error fetching current price for crypto " + externalId, e);
        }
    }

    private static CryptoResponseDto getCryptoResponseDto(PortfolioAsset crypto) {
        CryptoResponseDto dto = new CryptoResponseDto();
        dto.setId(crypto.getId());
        dto.setName(crypto.getAsset().getName());
        dto.setSymbol(crypto.getAsset().getSymbol());
        dto.setQuantity(crypto.getQuantity());
        dto.setPrice(crypto.getPriceWhenBought());
        dto.setOriginalValue(crypto.getPriceWhenBought() * crypto.getQuantity());
        dto.setCurrentPrice(crypto.getCurrentPrice());
        dto.setCurrentValue(crypto.getCurrentPrice() * crypto.getQuantity());
        dto.setPnl((crypto.getCurrentPrice() - crypto.getPriceWhenBought()) * crypto.getQuantity());
        dto.setPurchaseDate(crypto.getPurchaseDate());
        dto.setGroupName(crypto.getGroup() != null ? crypto.getGroup().getName() : null);
        return dto;
    }
}

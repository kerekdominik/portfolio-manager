package com.portfolio.controller;

import com.portfolio.dto.CryptoRequestDto;
import com.portfolio.dto.CryptoResponseDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.User;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.PortfolioAssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.impl.CryptoService;
import com.portfolio.service.ExternalCryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final PortfolioAssetRepository portfolioAssetRepository;
    private final GroupRepository groupRepository;
    private final CryptoListRepository cryptoListRepository;
    private final PortfolioRepository portfolioRepository;

    private final CryptoService cryptoService;
    private final ExternalCryptoService externalCryptoService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addCryptoToPortfolio(
            @RequestBody CryptoRequestDto cryptoRequest,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Optional<CryptoListItem> cryptoListItemOpt = cryptoListRepository.findById(cryptoRequest.getId());
        if (cryptoListItemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid asset ID"));
        }

        Crypto crypto = new Crypto(
                cryptoListItemOpt.get().getId(),         // CoinGecko ID
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
                cryptoRequest.getPurchaseDate() != null ? cryptoRequest.getPurchaseDate() : LocalDate.now()
        );

        // Set current price
        try {
            portfolioAsset.setCurrentPrice(externalCryptoService.getCryptoPriceInUsd(cryptoRequest.getId()).getPriceInUSD(cryptoRequest.getId()));
        } catch (Exception e) {
            log.error("Error fetching current price for crypto", e);
            throw new RuntimeException(e);
        }

        if (cryptoRequest.getGroupId() != null) {
            groupRepository.findById(cryptoRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of("message", "Crypto added to portfolio successfully"));
    }

    @GetMapping
    public ResponseEntity<List<CryptoResponseDto>> getAllCryptosInPortfolio(
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        List<PortfolioAsset> assets = portfolioAssetRepository.findByPortfolio(portfolio);
        List<CryptoResponseDto> cryptos = assets.stream()
                .filter(asset -> asset.getAsset() instanceof Crypto)
                .map(crypto -> {

                    try {
                        Crypto temp = (Crypto) crypto.getAsset();

                        crypto.setCurrentPrice(
                                externalCryptoService
                                        .getCryptoPriceInUsd((temp).getExternalId())
                                        .getPriceInUSD((temp).getExternalId())
                        );
                    } catch (Exception e) {
                        log.error("Error fetching current price for crypto", e);
                        throw new RuntimeException(e);
                    }

                    return getCryptoResponseDto(crypto);
                })
                .toList();

        return ResponseEntity.ok(cryptos);
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

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateCryptoInPortfolio(
            @PathVariable Long id,
            @RequestBody CryptoRequestDto cryptoRequest,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Crypto asset not found"));
        }

        PortfolioAsset portfolioAsset = portfolioAssetOpt.get();
        portfolioAsset.setQuantity(cryptoRequest.getQuantity());
        portfolioAsset.setPurchaseDate(cryptoRequest.getPurchaseDate());

        if (cryptoRequest.getGroupId() != null) {
            groupRepository.findById(cryptoRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of("message", "Crypto updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCryptoFromPortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isPresent()) {
            portfolioAssetRepository.delete(portfolioAssetOpt.get());
            return ResponseEntity.ok(Map.of("message", "Crypto deleted successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Crypto asset not found"));
    }
}

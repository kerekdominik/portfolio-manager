package com.portfolio.controller;

import com.portfolio.dto.CryptoRequestDto;
import com.portfolio.dto.CryptoResponseDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.PortfolioAssetRepository;
import com.portfolio.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final GroupRepository groupRepository;

    @PostMapping
    public ResponseEntity<Map<String, String>> addCryptoToPortfolio(
            @RequestBody CryptoRequestDto cryptoRequest,
            @AuthenticationPrincipal Portfolio portfolio) {

        Optional<Crypto> cryptoOpt = cryptoService.getCryptoById(cryptoRequest.getAssetId());
        if (cryptoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid asset ID"));
        }

        PortfolioAsset portfolioAsset = new PortfolioAsset();
        portfolioAsset.setPortfolio(portfolio);
        portfolioAsset.setAsset(cryptoOpt.get());
        portfolioAsset.setQuantity(cryptoRequest.getQuantity());
        portfolioAsset.setPurchaseDate(cryptoRequest.getPurchaseDate() != null ?
                cryptoRequest.getPurchaseDate() : LocalDateTime.now());

        if (cryptoRequest.getGroupId() != null) {
            groupRepository.findById(cryptoRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of("message", "Crypto added to portfolio successfully"));
    }

    @GetMapping
    public ResponseEntity<List<CryptoResponseDto>> getAllCryptosInPortfolio(
            @AuthenticationPrincipal Portfolio portfolio) {

        List<PortfolioAsset> assets = portfolioAssetRepository.findByPortfolio(portfolio);
        List<CryptoResponseDto> cryptos = assets.stream()
                .filter(asset -> asset.getAsset() instanceof Crypto)
                .map(asset -> {
                    CryptoResponseDto dto = new CryptoResponseDto();
                    dto.setId(asset.getId());
                    dto.setName(asset.getAsset().getName());
                    dto.setSymbol(asset.getAsset().getSymbol());
                    dto.setQuantity(asset.getQuantity());
                    dto.setPurchaseDate(asset.getPurchaseDate());
                    dto.setGroupName(asset.getGroup() != null ? asset.getGroup().getName() : null);
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(cryptos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateCryptoInPortfolio(
            @PathVariable Long id,
            @RequestBody CryptoRequestDto cryptoRequest,
            @AuthenticationPrincipal Portfolio portfolio) {

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
            @AuthenticationPrincipal Portfolio portfolio) {

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isPresent()) {
            portfolioAssetRepository.delete(portfolioAssetOpt.get());
            return ResponseEntity.ok(Map.of("message", "Crypto deleted successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Crypto asset not found"));
    }
}

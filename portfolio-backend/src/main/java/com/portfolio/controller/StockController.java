package com.portfolio.controller;

import com.portfolio.dto.StockRequestDto;
import com.portfolio.dto.StockResponseDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.User;
import com.portfolio.entity.asset.Stock;
import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.repository.GroupRepository;
import com.portfolio.repository.PortfolioAssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.repository.StockListRepository;
import com.portfolio.service.StockService;
import com.portfolio.service.ExternalStockService;
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
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final PortfolioAssetRepository portfolioAssetRepository;
    private final GroupRepository groupRepository;
    private final StockListRepository stockListRepository;
    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;
    private final ExternalStockService externalStockService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addStockToPortfolio(
            @RequestBody StockRequestDto stockRequest,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Optional<StockListItem> stockListItemOpt = stockListRepository.findBySymbol(stockRequest.getSymbol());
        if (stockListItemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid stock symbol"));
        }

        Stock stock = new Stock(
                stockListItemOpt.get().getSymbol(),
                stockListItemOpt.get().getName(),
                stockListItemOpt.get().getExchange()
        );
        stockService.saveStock(stock);

        PortfolioAsset portfolioAsset = new PortfolioAsset();
        portfolioAsset.setPortfolio(portfolio);
        portfolioAsset.setAsset(stock);
        portfolioAsset.setPriceWhenBought(stockRequest.getPrice());
        portfolioAsset.setQuantity(stockRequest.getQuantity());
        portfolioAsset.setPurchaseDate(
                stockRequest.getPurchaseDate() != null ? stockRequest.getPurchaseDate() : LocalDate.now()
        );

        try {
            portfolioAsset.setCurrentPrice(
                    externalStockService.getCurrentStockPriceInUSD(stockRequest.getSymbol())
            );
        } catch (Exception e) {
            log.error("Error fetching current price for stock", e);
            throw new RuntimeException(e);
        }

        if (stockRequest.getGroupId() != null) {
            groupRepository.findById(stockRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of("message", "Stock added to portfolio successfully"));
    }

    @GetMapping
    public ResponseEntity<List<StockResponseDto>> getAllStocksInPortfolio(
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        List<PortfolioAsset> assets = portfolioAssetRepository.findByPortfolio(portfolio);
        List<StockResponseDto> stocks = assets.stream()
                .filter(asset -> asset.getAsset() instanceof Stock)
                .map(this::getStockResponseDto)
                .toList();

        return ResponseEntity.ok(stocks);
    }

    private StockResponseDto getStockResponseDto(PortfolioAsset stock) {
        StockResponseDto dto = new StockResponseDto();
        dto.setId(stock.getId());
        dto.setName(stock.getAsset().getName());
        dto.setSymbol(stock.getAsset().getSymbol());
        dto.setPrice(stock.getPriceWhenBought());
        dto.setCurrentPrice(stock.getCurrentPrice());
        dto.setQuantity(stock.getQuantity());
        dto.setPurchaseDate(stock.getPurchaseDate());
        dto.setGroupName(stock.getGroup() != null ? stock.getGroup().getName() : null);
        return dto;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateStockInPortfolio(
            @PathVariable Long id,
            @RequestBody StockRequestDto stockRequest,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Stock asset not found"));
        }

        PortfolioAsset portfolioAsset = portfolioAssetOpt.get();
        portfolioAsset.setQuantity(stockRequest.getQuantity());
        portfolioAsset.setPurchaseDate(stockRequest.getPurchaseDate());

        if (stockRequest.getGroupId() != null) {
            groupRepository.findById(stockRequest.getGroupId()).ifPresent(portfolioAsset::setGroup);
        }

        portfolioAssetRepository.save(portfolioAsset);
        return ResponseEntity.ok(Map.of("message", "Stock updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStockFromPortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Optional<PortfolioAsset> portfolioAssetOpt = portfolioAssetRepository.findByIdAndPortfolio(id, portfolio);
        if (portfolioAssetOpt.isPresent()) {
            portfolioAssetRepository.delete(portfolioAssetOpt.get());
            return ResponseEntity.ok(Map.of("message", "Stock deleted successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Stock asset not found"));
    }
}

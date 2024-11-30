package com.portfolio.controller;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.impl.PnlCalculationService;
import com.portfolio.service.impl.PortfolioCompositionService;
import com.portfolio.service.impl.PortfolioValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "DashboardController", description = "API for dashboard-related operations")
public class DashboardController {

    private final PnlCalculationService pnlCalculationService;
    private final PortfolioCompositionService portfolioCompositionService;
    private final PortfolioValueService portfolioValueService;
    private final PortfolioRepository portfolioRepository;

    @GetMapping("/pnl-summary")
    @Operation(summary = "Get PNL summary", description = "Calculates and returns the total profit or loss for stocks and cryptocurrencies in the portfolio.")
    public ResponseEntity<Map<String, Double>> getPnlSummary(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> pnlSummary = pnlCalculationService.calculatePnlForPortfolio(portfolio);
        return ResponseEntity.ok(pnlSummary);
    }

    @GetMapping("/portfolio-composition")
    @Operation(summary = "Get portfolio composition", description = "Returns the portfolio composition by asset types (e.g., percentage of stocks vs cryptocurrencies).")
    public ResponseEntity<Map<String, Double>> getPortfolioComposition(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);
        return ResponseEntity.ok(composition);
    }

    @GetMapping("/group-pnl")
    @Operation(summary = "Get group-wise PNL", description = "Calculates and returns profit or loss for each asset group in the portfolio.")
    public ResponseEntity<Map<String, Double>> getGroupPnl(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> groupPnl = pnlCalculationService.calculatePnlForGroups(portfolio);
        return ResponseEntity.ok(groupPnl);
    }

    @GetMapping("/pnl-assets")
    @Operation(summary = "Get asset-wise PNL", description = "Returns profit or loss for each individual asset in the portfolio.")
    public ResponseEntity<Map<String, Double>> getPnlByAsset(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> pnlAssets = pnlCalculationService.calculatePnlByAsset(portfolio);
        return ResponseEntity.ok(pnlAssets);
    }

    @GetMapping("/portfolio-values")
    @Operation(summary = "Get portfolio values", description = "Returns the original value and current value of the entire portfolio.")
    public ResponseEntity<Map<String, Double>> getPortfolioValues(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> portfolioValues = portfolioValueService.calculatePortfolioValues(portfolio);
        return ResponseEntity.ok(portfolioValues);
    }
}

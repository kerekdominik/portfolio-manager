package com.portfolio.controller;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.service.impl.PnlCalculationService;
import com.portfolio.service.impl.PortfolioCompositionService;
import com.portfolio.service.impl.PortfolioValueService;
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
public class DashboardController {

    private final PnlCalculationService pnlCalculationService;
    private final PortfolioCompositionService portfolioCompositionService;
    private final PortfolioValueService portfolioValueService;
    private final PortfolioRepository portfolioRepository;

    @GetMapping("/pnl-summary")
    public ResponseEntity<Map<String, Double>> getPnlSummary(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> pnlSummary = pnlCalculationService.calculatePnlForPortfolio(portfolio);
        return ResponseEntity.ok(pnlSummary);
    }

    @GetMapping("/portfolio-composition")
    public ResponseEntity<Map<String, Double>> getPortfolioComposition(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);
        return ResponseEntity.ok(composition);
    }

    @GetMapping("/group-pnl")
    public ResponseEntity<Map<String, Double>> getGroupPnl(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> groupPnl = pnlCalculationService.calculatePnlForGroups(portfolio);
        return ResponseEntity.ok(groupPnl);
    }

    @GetMapping("/pnl-assets")
    public ResponseEntity<Map<String, Double>> getPnlByAsset(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> pnlAssets = pnlCalculationService.calculatePnlByAsset(portfolio);

        return ResponseEntity.ok(pnlAssets);
    }

    @GetMapping("/portfolio-values")
    public ResponseEntity<Map<String, Double>> getPortfolioValues(@AuthenticationPrincipal User user) {
        Portfolio portfolio = portfolioRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        Map<String, Double> portfolioValues = portfolioValueService.calculatePortfolioValues(portfolio);
        return ResponseEntity.ok(portfolioValues);
    }
}

package com.portfolio.service.impl;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.Stock;
import com.portfolio.repository.PortfolioAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PnlCalculationService {

    private final PortfolioAssetRepository portfolioAssetRepository;

    public Map<String, Double> calculatePnlForPortfolio(Portfolio portfolio) {
        double cryptoPnl = 0.0;
        double stockPnl = 0.0;

        // Fetch all assets for the portfolio
        for (PortfolioAsset asset : portfolioAssetRepository.findByPortfolio(portfolio)) {
            double pnl = (asset.getCurrentPrice() - asset.getPriceWhenBought()) * asset.getQuantity();

            // Aggregate PNL by asset type
            if (asset.getAsset() instanceof Crypto) {
                cryptoPnl += pnl;
            } else if (asset.getAsset() instanceof Stock) {
                stockPnl += pnl;
            }
        }

        Map<String, Double> pnlSummary = new HashMap<>();
        pnlSummary.put("cryptoPnl", cryptoPnl);
        pnlSummary.put("stockPnl", stockPnl);

        return pnlSummary;
    }

    public Map<String, Double> calculatePnlForGroups(Portfolio portfolio) {
        List<PortfolioAsset> assets = portfolioAssetRepository.findByPortfolio(portfolio);

        return assets.stream()
                .filter(asset -> asset.getGroup() != null) // Only consider grouped assets
                .collect(Collectors.groupingBy(
                        asset -> asset.getGroup().getName(),
                        Collectors.summingDouble(asset -> (asset.getCurrentPrice() - asset.getPriceWhenBought()) * asset.getQuantity())
                ));
    }
}

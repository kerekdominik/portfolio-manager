package com.portfolio.service.impl;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.enums.AssetCategory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioCompositionService {

    public Map<String, Double> calculateComposition(Portfolio portfolio) {
        List<PortfolioAsset> assets = portfolio.getPortfolioAssets();

        if (assets.isEmpty()) {
            return Map.of("cryptoPercentage", 0.0, "stockPercentage", 0.0);
        }

        long cryptoCount = assets.stream()
                .filter(asset -> asset.getAsset().getAssetCategory() == AssetCategory.CRYPTOCURRENCY)
                .count();
        long stockCount = assets.stream()
                .filter(asset -> asset.getAsset().getAssetCategory() == AssetCategory.STOCK)
                .count();
        long totalCount = assets.size();

        double cryptoPercentage = (double) cryptoCount / totalCount * 100;
        double stockPercentage = (double) stockCount / totalCount * 100;

        Map<String, Double> composition = new HashMap<>();
        composition.put("cryptoPercentage", cryptoPercentage);
        composition.put("stockPercentage", stockPercentage);

        return composition;
    }
}

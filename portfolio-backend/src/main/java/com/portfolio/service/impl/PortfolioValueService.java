package com.portfolio.service.impl;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.repository.PortfolioAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioValueService {

    private final PortfolioAssetRepository portfolioAssetRepository;

    public Map<String, Double> calculatePortfolioValues(Portfolio portfolio) {
        double originalValue = 0.0;
        double currentValue = 0.0;

        for (PortfolioAsset asset : portfolioAssetRepository.findByPortfolio(portfolio)) {
            originalValue += asset.getPriceWhenBought() * asset.getQuantity();
            currentValue += asset.getCurrentPrice() * asset.getQuantity();
        }

        Map<String, Double> values = new HashMap<>();
        values.put("Original Value", originalValue);
        values.put("Current Value", currentValue);

        return values;
    }
}

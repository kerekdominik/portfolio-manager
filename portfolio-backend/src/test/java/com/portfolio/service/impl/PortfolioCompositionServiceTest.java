package com.portfolio.service.impl;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioCompositionServiceTest {

    @InjectMocks
    private PortfolioCompositionService portfolioCompositionService;

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolioCompositionService = new PortfolioCompositionService();
        portfolio = new Portfolio();
        portfolio.setPortfolioAssets(new ArrayList<>());
    }

    @Test
    void testCalculateComposition_EmptyPortfolio() {
        // Act
        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);

        // Assert
        assertNotNull(composition);
        assertEquals(0.0, composition.get("cryptoPercentage"), 0.001);
        assertEquals(0.0, composition.get("stockPercentage"), 0.001);
    }

    @Test
    void testCalculateComposition_OnlyCryptoAssets() {
        // Arrange
        PortfolioAsset cryptoAsset1 = new PortfolioAsset();
        cryptoAsset1.setAsset(new Crypto("bitcoin", "Bitcoin", "BTC"));

        PortfolioAsset cryptoAsset2 = new PortfolioAsset();
        cryptoAsset2.setAsset(new Crypto("ethereum", "Ethereum", "ETH"));

        portfolio.getPortfolioAssets().add(cryptoAsset1);
        portfolio.getPortfolioAssets().add(cryptoAsset2);

        // Act
        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);

        // Assert
        assertNotNull(composition);
        assertEquals(100.0, composition.get("Crypto"), 0.001);
        assertEquals(0.0, composition.get("Stock"), 0.001);
    }

    @Test
    void testCalculateComposition_OnlyStockAssets() {
        // Arrange
        PortfolioAsset stockAsset1 = new PortfolioAsset();
        stockAsset1.setAsset(new Stock("AAPL", "Apple Inc.", "NASDAQ"));

        PortfolioAsset stockAsset2 = new PortfolioAsset();
        stockAsset2.setAsset(new Stock("MSFT", "Microsoft Corp.", "NASDAQ"));

        portfolio.getPortfolioAssets().add(stockAsset1);
        portfolio.getPortfolioAssets().add(stockAsset2);

        // Act
        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);

        // Assert
        assertNotNull(composition);
        assertEquals(0.0, composition.get("Crypto"), 0.001);
        assertEquals(100.0, composition.get("Stock"), 0.001);
    }

    @Test
    void testCalculateComposition_MixedAssets() {
        // Arrange
        PortfolioAsset cryptoAsset = new PortfolioAsset();
        cryptoAsset.setAsset(new Crypto("bitcoin", "Bitcoin", "BTC"));

        PortfolioAsset stockAsset = new PortfolioAsset();
        stockAsset.setAsset(new Stock("AAPL", "Apple Inc.", "NASDAQ"));

        portfolio.getPortfolioAssets().add(cryptoAsset);
        portfolio.getPortfolioAssets().add(stockAsset);

        // Act
        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);

        // Assert
        assertNotNull(composition);
        assertEquals(50.0, composition.get("Crypto"), 0.001);
        assertEquals(50.0, composition.get("Stock"), 0.001);
    }

    @Test
    void testCalculateComposition_VariedAssets() {
        // Arrange
        PortfolioAsset cryptoAsset1 = new PortfolioAsset();
        cryptoAsset1.setAsset(new Crypto("bitcoin", "Bitcoin", "BTC"));

        PortfolioAsset cryptoAsset2 = new PortfolioAsset();
        cryptoAsset2.setAsset(new Crypto("ethereum", "Ethereum", "ETH"));

        PortfolioAsset stockAsset = new PortfolioAsset();
        stockAsset.setAsset(new Stock("AAPL", "Apple Inc.", "NASDAQ"));

        portfolio.getPortfolioAssets().add(cryptoAsset1);
        portfolio.getPortfolioAssets().add(cryptoAsset2);
        portfolio.getPortfolioAssets().add(stockAsset);

        // Act
        Map<String, Double> composition = portfolioCompositionService.calculateComposition(portfolio);

        // Assert
        assertNotNull(composition);
        assertEquals(66.66, composition.get("Crypto"), 0.1);
        assertEquals(33.33, composition.get("Stock"), 0.1);
    }
}

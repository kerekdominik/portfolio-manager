package com.portfolio.service.impl;

import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.Stock;
import com.portfolio.repository.PortfolioAssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioValueServiceTest {

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;

    @InjectMocks
    private PortfolioValueService portfolioValueService;

    @Test
    void testCalculatePortfolioValues_WithAssets() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        Crypto crypto = new Crypto("bitcoin", "Bitcoin", "BTC");
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");

        PortfolioAsset cryptoAsset = new PortfolioAsset();
        cryptoAsset.setAsset(crypto);
        cryptoAsset.setPriceWhenBought(100.0);
        cryptoAsset.setCurrentPrice(150.0);
        cryptoAsset.setQuantity(2.0);

        PortfolioAsset stockAsset = new PortfolioAsset();
        stockAsset.setAsset(stock);
        stockAsset.setPriceWhenBought(200.0);
        stockAsset.setCurrentPrice(180.0);
        stockAsset.setQuantity(1.0);

        List<PortfolioAsset> assets = Arrays.asList(cryptoAsset, stockAsset);

        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(assets);

        // Act
        Map<String, Double> values = portfolioValueService.calculatePortfolioValues(portfolio);

        // Assert
        assertNotNull(values);
        assertEquals(400.0, values.get("Original Value"), 0.001); // (100*2) + (200*1)
        assertEquals(480.0, values.get("Current Value"), 0.001);  // (150*2) + (180*1)

        verify(portfolioAssetRepository).findByPortfolio(portfolio);
    }

    @Test
    void testCalculatePortfolioValues_EmptyPortfolio() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(List.of());

        // Act
        Map<String, Double> values = portfolioValueService.calculatePortfolioValues(portfolio);

        // Assert
        assertNotNull(values);
        assertEquals(0.0, values.get("Original Value"), 0.001);
        assertEquals(0.0, values.get("Current Value"), 0.001);

        verify(portfolioAssetRepository).findByPortfolio(portfolio);
    }

    @Test
    void testCalculatePortfolioValues_WithZeroQuantity() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        Crypto crypto = new Crypto("bitcoin", "Bitcoin", "BTC");

        PortfolioAsset cryptoAsset = new PortfolioAsset();
        cryptoAsset.setAsset(crypto);
        cryptoAsset.setPriceWhenBought(100.0);
        cryptoAsset.setCurrentPrice(150.0);
        cryptoAsset.setQuantity(0.0);

        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(List.of(cryptoAsset));

        // Act
        Map<String, Double> values = portfolioValueService.calculatePortfolioValues(portfolio);

        // Assert
        assertNotNull(values);
        assertEquals(0.0, values.get("Original Value"), 0.001);
        assertEquals(0.0, values.get("Current Value"), 0.001);

        verify(portfolioAssetRepository).findByPortfolio(portfolio);
    }
}

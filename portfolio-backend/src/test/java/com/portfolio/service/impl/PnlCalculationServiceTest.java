package com.portfolio.service.impl;

import com.portfolio.entity.Group;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.PortfolioAsset;
import com.portfolio.entity.asset.*;
import com.portfolio.entity.enums.AssetCategory;
import com.portfolio.repository.PortfolioAssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PnlCalculationServiceTest {

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;

    @InjectMocks
    private PnlCalculationService pnlCalculationService;

    @Test
    void testCalculatePnlForPortfolio() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        // Create Crypto and Stock assets using the provided constructors
        Crypto crypto = new Crypto("bitcoin", "Bitcoin", "BTC");
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");

        // Create PortfolioAssets
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
        Map<String, Double> pnlSummary = pnlCalculationService.calculatePnlForPortfolio(portfolio);

        // Assert
        assertNotNull(pnlSummary);
        assertEquals(100.0, pnlSummary.get("cryptoPnl"), 0.001); // (150 - 100) * 2
        assertEquals(-20.0, pnlSummary.get("stockPnl"), 0.001);  // (180 - 200) * 1

        verify(portfolioAssetRepository).findByPortfolio(portfolio);
    }

    @Test
    void testCalculatePnlForGroups() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        // Create groups
        Group groupA = new Group("Group A", null);
        Group groupB = new Group("Group B", null);

        // Create assets using the provided constructors
        Crypto crypto1 = new Crypto("bitcoin", "Bitcoin", "BTC");
        crypto1.setAssetCategory(AssetCategory.CRYPTOCURRENCY);

        Stock stock1 = new Stock("AAPL", "Apple Inc.", "NASDAQ");
        stock1.setAssetCategory(AssetCategory.STOCK);

        Crypto crypto2 = new Crypto("ethereum", "Ethereum", "ETH");
        crypto2.setAssetCategory(AssetCategory.CRYPTOCURRENCY);

        // Create PortfolioAssets
        PortfolioAsset portfolioAsset1 = new PortfolioAsset();
        portfolioAsset1.setAsset(crypto1);
        portfolioAsset1.setGroup(groupA);
        portfolioAsset1.setPriceWhenBought(100.0);
        portfolioAsset1.setCurrentPrice(120.0);
        portfolioAsset1.setQuantity(1.0);

        PortfolioAsset portfolioAsset2 = new PortfolioAsset();
        portfolioAsset2.setAsset(stock1);
        portfolioAsset2.setGroup(groupA);
        portfolioAsset2.setPriceWhenBought(200.0);
        portfolioAsset2.setCurrentPrice(190.0);
        portfolioAsset2.setQuantity(2.0);

        PortfolioAsset portfolioAsset3 = new PortfolioAsset();
        portfolioAsset3.setAsset(crypto2);
        portfolioAsset3.setGroup(groupB);
        portfolioAsset3.setPriceWhenBought(50.0);
        portfolioAsset3.setCurrentPrice(75.0);
        portfolioAsset3.setQuantity(1.5);

        PortfolioAsset assetWithoutGroup = new PortfolioAsset();
        Stock stock2 = new Stock("MSFT", "Microsoft Corp.", "NASDAQ");
        stock2.setAssetCategory(AssetCategory.STOCK);
        assetWithoutGroup.setAsset(stock2);
        assetWithoutGroup.setPriceWhenBought(80.0);
        assetWithoutGroup.setCurrentPrice(90.0);
        assetWithoutGroup.setQuantity(1.0);

        // Add assets to groups
        groupA.setAssets(new ArrayList<>());
        groupA.addAsset(portfolioAsset1);
        groupA.addAsset(portfolioAsset2);

        groupB.setAssets(new ArrayList<>());
        groupB.addAsset(portfolioAsset3);

        List<PortfolioAsset> assets = Arrays.asList(portfolioAsset1, portfolioAsset2, portfolioAsset3, assetWithoutGroup);

        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(assets);

        // Act
        Map<String, Double> pnlByGroup = pnlCalculationService.calculatePnlForGroups(portfolio);

        // Assert
        assertNotNull(pnlByGroup);
        assertEquals(0.0, pnlByGroup.get("Group A"), 0.001);  // (20 * 1) + (-10 * 2)
        assertEquals(37.5, pnlByGroup.get("Group B"), 0.001); // (25 * 1.5)
        assertFalse(pnlByGroup.containsKey(null));

        verify(portfolioAssetRepository).findByPortfolio(portfolio);
    }

    @Test
    void testCalculatePnlByAsset() {
        // Arrange
        Portfolio portfolio = new Portfolio();

        // Create assets using the provided constructors
        Crypto crypto = new Crypto("bitcoin", "Bitcoin", "BTC");
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");

        // Create PortfolioAssets
        PortfolioAsset portfolioAsset1 = new PortfolioAsset();
        portfolioAsset1.setAsset(crypto);
        portfolioAsset1.setPriceWhenBought(100.0);
        portfolioAsset1.setCurrentPrice(110.0);
        portfolioAsset1.setQuantity(1.0);

        PortfolioAsset portfolioAsset2 = new PortfolioAsset();
        portfolioAsset2.setAsset(stock);
        portfolioAsset2.setPriceWhenBought(50.0);
        portfolioAsset2.setCurrentPrice(60.0);
        portfolioAsset2.setQuantity(2.0);

        List<PortfolioAsset> assets = Arrays.asList(portfolioAsset1, portfolioAsset2);

        when(portfolioAssetRepository.findByPortfolio(portfolio)).thenReturn(assets);

        // Act
        Map<String, Double> pnlByAsset = pnlCalculationService.calculatePnlByAsset(portfolio);

        // Assert
        assertNotNull(pnlByAsset);
        assertEquals(10.0, pnlByAsset.get("Bitcoin"), 0.001);    // (110 - 100) * 1
        assertEquals(20.0, pnlByAsset.get("Apple Inc."), 0.001); // (60 - 50) * 2

        verify(portfolioAssetRepository).findByPortfolio(portfolio);
    }
}

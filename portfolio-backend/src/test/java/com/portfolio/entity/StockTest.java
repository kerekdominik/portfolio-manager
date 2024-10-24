package com.portfolio.entity;

import com.portfolio.entity.asset.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {
    private Portfolio mockPortfolio;

    @BeforeEach
    void setUp() {
        mockPortfolio = Mockito.mock(Portfolio.class);
    }

    @Test
    void testNoArgsConstructor() {
        Stock stock = new Stock();
        assertNotNull(stock);
    }

    @Test
    void testAllArgsConstructor() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setSymbol("AAPL");
        stock.setName("Apple Inc.");
        stock.setPriceWhenBought(145.86);
        stock.setPriceNow(150.00);
        stock.setPortfolio(mockPortfolio);
        stock.setPurchaseDate(LocalDateTime.now());

        assertEquals(1L, stock.getId());
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
        assertEquals(145.86, stock.getPriceWhenBought());
        assertEquals(150.00, stock.getPriceNow());
        assertEquals(mockPortfolio, stock.getPortfolio());
        assertNotNull(stock.getPurchaseDate());
    }
}
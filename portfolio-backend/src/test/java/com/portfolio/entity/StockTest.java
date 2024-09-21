package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        Stock stock = new Stock(1L, "AAPL", "Apple Inc.", 145.86, 0.86, 0.59, mockPortfolio);

        assertEquals(1L, stock.getId());
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
        assertEquals(145.86, stock.getPrice());
        assertEquals(0.86, stock.getChange());
        assertEquals(0.59, stock.getPercentChange());
        assertEquals(mockPortfolio, stock.getPortfolio());
    }

    @Test
    void testSettersAndGetters() {
        Stock stock = new Stock();

        stock.setId(1L);
        stock.setSymbol("AAPL");
        stock.setName("Apple Inc.");
        stock.setPrice(145.86);
        stock.setChange(0.86);
        stock.setPercentChange(0.59);
        stock.setPortfolio(mockPortfolio);

        assertEquals(1L, stock.getId());
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getName());
        assertEquals(145.86, stock.getPrice());
        assertEquals(0.86, stock.getChange());
        assertEquals(0.59, stock.getPercentChange());
        assertEquals(mockPortfolio, stock.getPortfolio());
    }
}
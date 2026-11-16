package com.portfolio.entity;

import com.portfolio.entity.asset.Stock;
import com.portfolio.entity.enums.AssetCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {
    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = new Stock();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(stock, "Stock object should not be null with no-args constructor.");
    }

    @Test
    void testSettersAndGetters() {
        stock.setId(1L);
        stock.setSymbol("AAPL");
        stock.setName("Apple Inc.");

        assertEquals(1L, stock.getId(), "ID should be 1");
        assertEquals("AAPL", stock.getSymbol(), "Symbol should be AAPL");
        assertEquals("Apple Inc.", stock.getName(), "Name should be Apple Inc.");
    }

    @Test
    void testConstructorSetsFieldsAndCategory() {
        Stock aapl = new Stock("AAPL", "Apple Inc.", "NASDAQ");

        assertEquals("AAPL", aapl.getSymbol());
        assertEquals("Apple Inc.", aapl.getName());
        assertEquals("NASDAQ", aapl.getExchange());
        assertEquals(AssetCategory.STOCK, aapl.getAssetCategory());
    }
}

package com.portfolio.entity;

import com.portfolio.entity.asset.Cryptocurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CryptocurrencyTest {
    private Portfolio mockPortfolio;

    @BeforeEach
    void setUp() {
        mockPortfolio = Mockito.mock(Portfolio.class);
    }

    @Test
    void testNoArgsConstructor() {
        Cryptocurrency cryptocurrency = new Cryptocurrency();
        assertNotNull(cryptocurrency);
    }

    @Test
    void testAllArgsConstructor() {
        Cryptocurrency cryptocurrency = new Cryptocurrency();
        cryptocurrency.setId(1L);
        cryptocurrency.setSymbol("BTC");
        cryptocurrency.setName("Bitcoin");
        cryptocurrency.setPriceWhenBought(45000.0);
        cryptocurrency.setPriceNow(10000.0);
        cryptocurrency.setPortfolio(mockPortfolio);

        assertEquals(1L, cryptocurrency.getId());
        assertEquals("BTC", cryptocurrency.getSymbol());
        assertEquals("Bitcoin", cryptocurrency.getName());
        assertEquals(45000.0, cryptocurrency.getPriceWhenBought());
        assertEquals(10000.0, cryptocurrency.getPriceNow());
        assertEquals(mockPortfolio, cryptocurrency.getPortfolio());
    }
}
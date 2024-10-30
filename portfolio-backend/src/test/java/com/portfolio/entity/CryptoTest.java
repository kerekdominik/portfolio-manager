package com.portfolio.entity;

import com.portfolio.entity.asset.Crypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CryptoTest {
    private Portfolio mockPortfolio;

    @BeforeEach
    void setUp() {
        mockPortfolio = Mockito.mock(Portfolio.class);
    }

    @Test
    void testNoArgsConstructor() {
        Crypto crypto = new Crypto();
        assertNotNull(crypto);
    }

    @Test
    void testAllArgsConstructor() {
        Crypto crypto = new Crypto();
        crypto.setId(1L);
        crypto.setSymbol("BTC");
        crypto.setName("Bitcoin");
        crypto.setPriceWhenBought(45000.0);
        crypto.setPriceNow(10000.0);
        crypto.setPortfolio(mockPortfolio);

        assertEquals(1L, crypto.getId());
        assertEquals("BTC", crypto.getSymbol());
        assertEquals("Bitcoin", crypto.getName());
        assertEquals(45000.0, crypto.getPriceWhenBought());
        assertEquals(10000.0, crypto.getPriceNow());
        assertEquals(mockPortfolio, crypto.getPortfolio());
    }
}
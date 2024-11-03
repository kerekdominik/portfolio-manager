package com.portfolio.entity;

import com.portfolio.entity.asset.Crypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CryptoTest {
    private Portfolio mockPortfolio;
    private Crypto crypto;

    @BeforeEach
    void setUp() {
        mockPortfolio = Mockito.mock(Portfolio.class);
        crypto = new Crypto();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(crypto, "Crypto object should not be null with no-args constructor.");
    }

    @Test
    void testSettersAndGetters() {
        crypto.setId(1L);
        crypto.setSymbol("BTC");
        crypto.setName("Bitcoin");
        crypto.setPortfolio(mockPortfolio);

        assertEquals(1L, crypto.getId(), "ID should be 1");
        assertEquals("BTC", crypto.getSymbol(), "Symbol should be BTC");
        assertEquals("Bitcoin", crypto.getName(), "Name should be Bitcoin");
        assertEquals(mockPortfolio, crypto.getPortfolio(), "Portfolio should match mockPortfolio");
    }
}

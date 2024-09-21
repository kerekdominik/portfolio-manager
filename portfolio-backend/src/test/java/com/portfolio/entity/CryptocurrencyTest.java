package com.portfolio.entity;

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
        Cryptocurrency cryptocurrency = new Cryptocurrency(1L, "BTC", "Bitcoin", 45000.0, 0.0, mockPortfolio);

        assertEquals(1L, cryptocurrency.getId());
        assertEquals("BTC", cryptocurrency.getSymbol());
        assertEquals("Bitcoin", cryptocurrency.getName());
        assertEquals(45000.0, cryptocurrency.getPrice());
        assertEquals(0.0, cryptocurrency.getChange());
        assertEquals(mockPortfolio, cryptocurrency.getPortfolio());
    }

    @Test
    void testSettersAndGetters() {
        Cryptocurrency cryptocurrency = new Cryptocurrency();

        cryptocurrency.setId(1L);
        cryptocurrency.setSymbol("BTC");
        cryptocurrency.setName("Bitcoin");
        cryptocurrency.setPrice(45000.0);
        cryptocurrency.setChange(0.0);
        cryptocurrency.setPortfolio(mockPortfolio);

        assertEquals(1L, cryptocurrency.getId());
        assertEquals("BTC", cryptocurrency.getSymbol());
        assertEquals("Bitcoin", cryptocurrency.getName());
        assertEquals(45000.0, cryptocurrency.getPrice());
        assertEquals(0.0, cryptocurrency.getChange());
        assertEquals(mockPortfolio, cryptocurrency.getPortfolio());
    }

}
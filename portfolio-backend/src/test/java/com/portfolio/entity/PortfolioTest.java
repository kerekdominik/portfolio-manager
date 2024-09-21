package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {
    private User mockUser;
    private List<Stock> mockStocks;
    private List<Cryptocurrency> mockCryptocurrencies;
    private List<Group> mockGroups;

    @BeforeEach
    void setUp() {
        mockUser = Mockito.mock(User.class);
        mockStocks = List.of(Mockito.mock(Stock.class));
        mockCryptocurrencies = List.of(Mockito.mock(Cryptocurrency.class));
        mockGroups = List.of(Mockito.mock(Group.class));
    }

    @Test
    void testNoArgsConstructor() {
        Portfolio portfolio = new Portfolio();
        assertNotNull(portfolio);
    }

    @Test
    void testAllArgsConstructor() {
        Portfolio portfolio = new Portfolio(1L, mockUser, mockStocks, mockCryptocurrencies, mockGroups);

        assertEquals(1L, portfolio.getId());
        assertEquals(mockUser, portfolio.getUser());
        assertEquals(mockStocks, portfolio.getStocks());
        assertEquals(mockCryptocurrencies, portfolio.getCryptocurrencies());
        assertEquals(mockGroups, portfolio.getGroups());
    }

    @Test
    void testSettersAndGetters() {
        Portfolio portfolio = new Portfolio();

        portfolio.setId(1L);
        portfolio.setUser(mockUser);
        portfolio.setStocks(mockStocks);
        portfolio.setCryptocurrencies(mockCryptocurrencies);
        portfolio.setGroups(mockGroups);

        assertEquals(1L, portfolio.getId());
        assertEquals(mockUser, portfolio.getUser());
        assertEquals(mockStocks, portfolio.getStocks());
        assertEquals(mockCryptocurrencies, portfolio.getCryptocurrencies());
        assertEquals(mockGroups, portfolio.getGroups());
    }
}
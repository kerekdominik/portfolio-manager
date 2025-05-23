package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {
    private User mockUser;
    private List<PortfolioAsset> mockPortfolioAssets;

    @BeforeEach
    void setUp() {
        mockUser = Mockito.mock(User.class);
        mockPortfolioAssets = List.of(Mockito.mock(PortfolioAsset.class));
    }

    @Test
    void testNoArgsConstructor() {
        Portfolio portfolio = new Portfolio();
        assertNotNull(portfolio);
    }

    @Test
    void testAllArgsConstructor() {
        Portfolio portfolio = new Portfolio(1L, mockUser, mockPortfolioAssets);

        assertEquals(1L, portfolio.getId());
        assertEquals(mockUser, portfolio.getUser());
        assertEquals(mockPortfolioAssets, portfolio.getPortfolioAssets());
    }

    @Test
    void testSettersAndGetters() {
        Portfolio portfolio = new Portfolio();

        portfolio.setId(1L);
        portfolio.setUser(mockUser);
        portfolio.setPortfolioAssets(mockPortfolioAssets);

        assertEquals(1L, portfolio.getId());
        assertEquals(mockUser, portfolio.getUser());
        assertEquals(mockPortfolioAssets, portfolio.getPortfolioAssets());
    }
}
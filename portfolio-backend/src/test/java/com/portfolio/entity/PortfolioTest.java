package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {
    private User mockUser;
    private List<Transaction> mockTransactions;
    private List<PortfolioAsset> mockPortfolioAssets;
    private List<Group> mockGroups;

    @BeforeEach
    void setUp() {
        mockUser = Mockito.mock(User.class);
        mockTransactions = List.of(Mockito.mock(Transaction.class));
        mockPortfolioAssets = List.of(Mockito.mock(PortfolioAsset.class));
        mockGroups = List.of(Mockito.mock(Group.class));
    }

    @Test
    void testNoArgsConstructor() {
        Portfolio portfolio = new Portfolio();
        assertNotNull(portfolio);
    }

    @Test
    void testAllArgsConstructor() {
        Portfolio portfolio = new Portfolio(1L, mockUser, mockTransactions, mockPortfolioAssets, mockGroups);

        assertEquals(1L, portfolio.getId());
        assertEquals(mockUser, portfolio.getUser());
        assertEquals(mockTransactions, portfolio.getTransactions());
        assertEquals(mockPortfolioAssets, portfolio.getPortfolioAssets());
        assertEquals(mockGroups, portfolio.getGroups());
    }

    @Test
    void testSettersAndGetters() {
        Portfolio portfolio = new Portfolio();

        portfolio.setId(1L);
        portfolio.setUser(mockUser);
        portfolio.setTransactions(mockTransactions);
        portfolio.setPortfolioAssets(mockPortfolioAssets);
        portfolio.setGroups(mockGroups);

        assertEquals(1L, portfolio.getId());
        assertEquals(mockUser, portfolio.getUser());
        assertEquals(mockTransactions, portfolio.getTransactions());
        assertEquals(mockPortfolioAssets, portfolio.getPortfolioAssets());
        assertEquals(mockGroups, portfolio.getGroups());
    }
}
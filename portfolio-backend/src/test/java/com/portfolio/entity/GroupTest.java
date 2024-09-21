package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {
    private Portfolio mockPortfolio;

    @BeforeEach
    void setUp() {
        mockPortfolio = org.mockito.Mockito.mock(Portfolio.class);
    }

    @Test
    void testNoArgsConstructor() {
        Group group = new Group();
        assertNotNull(group);
    }

    @Test
    void testAllArgsConstructor() {
        Group group = new Group(1L, "Tech", mockPortfolio);

        assertEquals(1L, group.getId());
        assertEquals("Tech", group.getName());
        assertEquals(mockPortfolio, group.getPortfolio());
    }

    @Test
    void testSettersAndGetters() {
        Group group = new Group();

        group.setId(1L);
        group.setName("Tech");
        group.setPortfolio(mockPortfolio);

        assertEquals(1L, group.getId());
        assertEquals("Tech", group.getName());
        assertEquals(mockPortfolio, group.getPortfolio());
    }
}
package com.portfolio.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class GroupTest {

    private Group group;
    private PortfolioAsset mockPortfolioAsset;

    @BeforeEach
    void setUp() {
        group = new Group();
        mockPortfolioAsset = Mockito.mock(PortfolioAsset.class);
    }

    @Test
    void testNoArgsConstructor() {
        Group groupNoArgs = new Group();
        assertNotNull(groupNoArgs);
    }

    @Test
    void testAllArgsConstructor() {
        Group groupWithArgs = new Group(1L, "Tech", new ArrayList<>(List.of(mockPortfolioAsset)));

        assertEquals(1L, groupWithArgs.getId());
        assertEquals("Tech", groupWithArgs.getName());
        assertEquals(1, groupWithArgs.getAssets().size());
        assertEquals(mockPortfolioAsset, groupWithArgs.getAssets().get(0));
    }

    @Test
    void testSettersAndGetters() {
        group.setId(1L);
        group.setName("Tech");
        group.setAssets(new ArrayList<>(List.of(mockPortfolioAsset)));

        assertEquals(1L, group.getId());
        assertEquals("Tech", group.getName());
        assertEquals(1, group.getAssets().size());
        assertEquals(mockPortfolioAsset, group.getAssets().get(0));
    }

    @Test
    void testAddAsset() {
        group.setAssets(new ArrayList<>()); // Initialize the assets list

        // Define behavior for mockPortfolioAsset.setGroup
        doAnswer(invocation -> {
            Group assignedGroup = invocation.getArgument(0);
            when(mockPortfolioAsset.getGroup()).thenReturn(assignedGroup);
            return null;
        }).when(mockPortfolioAsset).setGroup(group);

        group.addAsset(mockPortfolioAsset);

        assertEquals(1, group.getAssets().size());
        assertEquals(mockPortfolioAsset, group.getAssets().get(0));
        assertEquals(group, mockPortfolioAsset.getGroup()); // Verify bidirectional relationship
    }

    @Test
    void testRemoveAsset() {
        group.setAssets(new ArrayList<>(List.of(mockPortfolioAsset)));
        Mockito.doNothing().when(mockPortfolioAsset).setGroup(null);

        group.removeAsset(mockPortfolioAsset);

        assertEquals(0, group.getAssets().size());
        Mockito.verify(mockPortfolioAsset).setGroup(null); // Verify that group is set to null
    }
}

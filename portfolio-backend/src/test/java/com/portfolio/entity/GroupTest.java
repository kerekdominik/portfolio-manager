package com.portfolio.entity;

import com.portfolio.entity.asset.CommonAsset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {
    private CommonAsset mockAsset;

    @BeforeEach
    void setUp() {
        mockAsset = Mockito.mock(CommonAsset.class);
    }

    @Test
    void testNoArgsConstructor() {
        Group group = new Group();
        assertNotNull(group);
    }

    @Test
    void testAllArgsConstructor() {
        Group group = new Group(1L, "Tech", List.of(mockAsset));

        assertEquals(1L, group.getId());
        assertEquals("Tech", group.getName());
        assertEquals(List.of(mockAsset), group.getAssets());
    }

    @Test
    void testSettersAndGetters() {
        Group group = new Group();

        group.setId(1L);
        group.setName("Tech");
        group.setAssets(List.of(mockAsset));

        assertEquals(1L, group.getId());
        assertEquals("Tech", group.getName());
        assertEquals(List.of(mockAsset), group.getAssets());
    }
}
package com.portfolio.entity;

import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.enums.AssetCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoTest {
    private Crypto crypto;

    @BeforeEach
    void setUp() {
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

        assertEquals(1L, crypto.getId(), "ID should be 1");
        assertEquals("BTC", crypto.getSymbol(), "Symbol should be BTC");
        assertEquals("Bitcoin", crypto.getName(), "Name should be Bitcoin");
    }

    @Test
    void testConstructorSetsFieldsAndCategory() {
        Crypto bitcoin = new Crypto("bitcoin", "Bitcoin", "btc");

        assertEquals("bitcoin", bitcoin.getExternalId());
        assertEquals("Bitcoin", bitcoin.getName());
        assertEquals("btc", bitcoin.getSymbol());
        assertEquals(AssetCategory.CRYPTOCURRENCY, bitcoin.getAssetCategory());
    }
}

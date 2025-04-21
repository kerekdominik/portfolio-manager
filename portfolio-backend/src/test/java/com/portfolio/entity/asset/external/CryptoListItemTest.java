package com.portfolio.entity.asset.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoListItemTest {

    private CryptoListItem cryptoListItem;

    @BeforeEach
    void setUp() {
        cryptoListItem = new CryptoListItem();
    }

    @Test
    void testNoArgsConstructor() {
        CryptoListItem item = new CryptoListItem();
        assertNotNull(item);
    }

    @Test
    void testAllArgsConstructor() {
        CryptoListItem item = new CryptoListItem("bitcoin", "btc", "Bitcoin");
        
        assertEquals("bitcoin", item.getId());
        assertEquals("btc", item.getSymbol());
        assertEquals("Bitcoin", item.getName());
    }

    @Test
    void testSettersAndGetters() {
        cryptoListItem.setId("ethereum");
        cryptoListItem.setSymbol("eth");
        cryptoListItem.setName("Ethereum");
        
        assertEquals("ethereum", cryptoListItem.getId());
        assertEquals("eth", cryptoListItem.getSymbol());
        assertEquals("Ethereum", cryptoListItem.getName());
    }

    @Test
    void testBuilder() {
        CryptoListItem item = CryptoListItem.builder()
                .id("solana")
                .symbol("sol")
                .name("Solana")
                .build();
        
        assertEquals("solana", item.getId());
        assertEquals("sol", item.getSymbol());
        assertEquals("Solana", item.getName());
    }

    @Test
    void testEqualsAndHashCode() {
        CryptoListItem item1 = new CryptoListItem("cardano", "ada", "Cardano");
        CryptoListItem item2 = new CryptoListItem("cardano", "ada", "Cardano");
        CryptoListItem item3 = new CryptoListItem("polkadot", "dot", "Polkadot");
        
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        
        assertNotEquals(item1, item3);
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }

    @Test
    void testToString() {
        cryptoListItem.setId("dogecoin");
        cryptoListItem.setSymbol("doge");
        cryptoListItem.setName("Dogecoin");
        
        String toString = cryptoListItem.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("id=dogecoin"));
        assertTrue(toString.contains("symbol=doge"));
        assertTrue(toString.contains("name=Dogecoin"));
    }
}
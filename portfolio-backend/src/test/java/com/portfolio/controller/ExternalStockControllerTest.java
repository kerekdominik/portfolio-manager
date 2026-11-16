package com.portfolio.controller;

import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.repository.StockListItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalStockControllerTest {

    @Mock
    private StockListItemRepository stockListRepository;

    @InjectMocks
    private ExternalStockController controller;

    @Test
    void getAllStockDetails_mapsListItems() {
        when(stockListRepository.findAll())
                .thenReturn(List.of(new StockListItem("AAPL", "Apple Inc.", "NASDAQ", "Stock", "Active")));

        ResponseEntity<List<Map<String, String>>> response = controller.getAllStockDetails();

        assertEquals(List.of(Map.of(
                "symbol", "AAPL",
                "name", "Apple Inc.",
                "exchange", "NASDAQ",
                "assetType", "Stock",
                "status", "Active")), response.getBody());
    }
}

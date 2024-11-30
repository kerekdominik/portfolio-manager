package com.portfolio.controller;

import com.portfolio.repository.StockListItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "ExternalStockController", description = "API for external stock data management")
public class ExternalStockController {

    private final StockListItemRepository stockListRepository;

    @GetMapping("/list")
    @Operation(
            summary = "Retrieve all stock details",
            description = "Fetches a list of all available stocks from the database, including symbol, name, exchange, asset type, and status."
    )
    public ResponseEntity<List<Map<String, String>>> getAllStockDetails() {
        List<Map<String, String>> stockDetails = stockListRepository.findAll()
                .stream()
                .map(stock -> {
                    Map<String, String> details = new HashMap<>();
                    details.put("symbol", stock.getSymbol());
                    details.put("name", stock.getName());
                    details.put("exchange", stock.getExchange());
                    details.put("assetType", stock.getAssetType());
                    details.put("status", stock.getStatus());
                    return details;
                })
                .toList();
        return ResponseEntity.ok(stockDetails);
    }
}

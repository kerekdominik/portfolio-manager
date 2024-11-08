package com.portfolio.controller;

import com.portfolio.repository.StockListItemRepository;
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
public class ExternalStockController {

    private final StockListItemRepository stockListRepository;

    @GetMapping("/list")
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

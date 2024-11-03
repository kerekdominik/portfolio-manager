package com.portfolio.controller;

import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.service.ExternalCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class ExternalCryptoController {

    private final ExternalCryptoService externalCryptoService;
    private final CryptoListRepository cryptoListRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, String>>> getAllCryptoDetails() {
        List<Map<String, String>> cryptoDetails = cryptoListRepository.findAll()
                .stream()
                .map(crypto -> {
                    Map<String, String> details = new HashMap<>();
                    details.put("id", crypto.getId());
                    details.put("name", crypto.getName());
                    details.put("symbol", crypto.getSymbol());
                    return details;
                })
                .toList();
        return ResponseEntity.ok(cryptoDetails);
    }

    @GetMapping("/current/price")
    public ResponseEntity<Map<String, Object>> getCryptoPrice(@RequestParam String id) {
        try {
            CurrentCryptoResponse currentCryptoResponse = externalCryptoService.getCryptoPriceInUsd(id.toLowerCase());
            Double price = currentCryptoResponse.getPriceInUSD(id.toLowerCase());

            Map<String, Object> response = new HashMap<>();
            response.put("cryptoId", id);
            response.put("price", price);

            return price != null
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(404).body(Map.of("error", "Price not found for cryptocurrency: " + id));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error fetching price: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body(Map.of("error", "Request interrupted: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid cryptocurrency id: " + e.getMessage()));
        }
    }

    @GetMapping("/historical/price")
    public ResponseEntity<Map<String, Object>> getHistoricalPrice(@RequestParam String id, @RequestParam String date) {
        try {
            HistoricalCryptoResponse historicalCryptoResponse = externalCryptoService.getHistoricalPrice(id.toLowerCase(), date);
            Double price = historicalCryptoResponse.getMarketData().getPriceInUSD();

            Map<String, Object> response = new HashMap<>();
            response.put("cryptoId", id);
            response.put("date", date);
            response.put("price", price);

            return price != null
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(404).body(Map.of("error", "Historical price not found for cryptocurrency: " + id + " on date: " + date));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error fetching historical price: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body(Map.of("error", "Request interrupted: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid date format or other issue: " + e.getMessage()));
        }
    }
}

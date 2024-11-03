package com.portfolio.controller;

import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.service.ExternalCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class ExternalCryptoController {

    private final ExternalCryptoService externalCryptoService;
    private final CryptoListRepository cryptoListRepository;

    @GetMapping("/list")
    public ResponseEntity<List<String>> getAllCryptoNames() {
        List<String> cryptoNames = cryptoListRepository.findAll()
                .stream()
                .map(CryptoListItem::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cryptoNames);
    }

    @GetMapping("/current/price")
    public ResponseEntity<?> getCryptoPrice(@RequestParam String id) {
        try {
            CurrentCryptoResponse currentCryptoResponse = externalCryptoService.getCryptoPriceInUsd(id.toLowerCase());
            Double price = currentCryptoResponse.getPriceInUSD(id.toLowerCase());

            if (price != null) {
                return ResponseEntity.ok(price);
            } else {
                return ResponseEntity.status(404).body("Price not found for cryptocurrency: " + id);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching price: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Request interrupted: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/historical/price")
    public ResponseEntity<?> getHistoricalPrice(@RequestParam String id, @RequestParam String date) {
        try {
            HistoricalCryptoResponse historicalCryptoResponse = externalCryptoService.getHistoricalPrice(id.toLowerCase(), date);
            Double price = historicalCryptoResponse.getMarketData().getPriceInUSD();

            if (price != null) {
                return ResponseEntity.ok(price);
            } else {
                return ResponseEntity.status(404).body("Historical price not found for cryptocurrency: " + id + " on date: " + date);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching historical price: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Request interrupted: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}

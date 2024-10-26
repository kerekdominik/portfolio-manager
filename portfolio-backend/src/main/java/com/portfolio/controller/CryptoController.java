package com.portfolio.controller;

import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @GetMapping("/current/price")
    public ResponseEntity<?> getCryptoPrice(@RequestParam String id) {
        try {
            CurrentCryptoResponse currentCryptoResponse = cryptoService.getCryptoPriceInUsd(id.toLowerCase());
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
            HistoricalCryptoResponse historicalCryptoResponse = cryptoService.getHistoricalPrice(id.toLowerCase(), date);
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

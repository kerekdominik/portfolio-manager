package com.portfolio.controller;

import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.CryptoListItem;
import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.service.CryptoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;
    private final CryptoListRepository cryptoListRepository;

    @GetMapping("/user")
    @Operation(summary = "Get all cryptocurrencies for a user", description = "Returns a list of all cryptocurrencies for a given user.")
    public ResponseEntity<List<Crypto>> getCryptosByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok(cryptoService.getCryptosByUserId(userId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> getAllCryptoNames() {
        List<String> cryptoNames = cryptoListRepository.findAll()
                .stream()
                .map(CryptoListItem::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cryptoNames);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Crypto> getCryptoById(@PathVariable Long id) {
        return cryptoService.getCryptoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Crypto> createCrypto(@RequestBody Crypto crypto) {
        return ResponseEntity.ok(cryptoService.saveCrypto(crypto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Crypto> updateCrypto(@PathVariable Long id, @RequestBody Crypto crypto) {
        return ResponseEntity.ok(cryptoService.updateCrypto(id, crypto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrypto(@PathVariable Long id) {
        cryptoService.deleteCrypto(id);
        return ResponseEntity.noContent().build();
    }

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

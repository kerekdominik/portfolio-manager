package com.portfolio.controller;

import com.portfolio.dto.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.dto.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.service.ExternalCryptoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalCryptoControllerTest {

    @Mock
    private ExternalCryptoService externalCryptoService;

    @Mock
    private CryptoListRepository cryptoListRepository;

    @InjectMocks
    private ExternalCryptoController controller;

    @AfterEach
    void clearInterruptFlag() {
        Thread.interrupted();
    }

    private static HistoricalCryptoResponse historical(Double usd) {
        HistoricalCryptoResponse.MarketData marketData = new HistoricalCryptoResponse.MarketData();
        marketData.setCurrentPrice(usd == null ? Map.of() : Map.of("usd", usd));
        HistoricalCryptoResponse response = new HistoricalCryptoResponse();
        response.setMarketData(marketData);
        return response;
    }

    @Test
    void getAllCryptoDetails_mapsListItems() {
        when(cryptoListRepository.findAll()).thenReturn(List.of(new CryptoListItem("bitcoin", "btc", "Bitcoin")));

        ResponseEntity<List<Map<String, String>>> response = controller.getAllCryptoDetails();

        assertEquals(List.of(Map.of("id", "bitcoin", "name", "Bitcoin", "symbol", "btc")), response.getBody());
    }

    @Test
    void getCryptoPrice_returnsPriceForLowercasedId() throws Exception {
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin"))
                .thenReturn(new CurrentCryptoResponse(Map.of("bitcoin", Map.of("usd", 50000.0))));

        ResponseEntity<Map<String, Object>> response = controller.getCryptoPrice("Bitcoin");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Bitcoin", response.getBody().get("cryptoId"));
        assertEquals(50000.0, response.getBody().get("price"));
    }

    @Test
    void getCryptoPrice_unknownCrypto_returns404() throws Exception {
        when(externalCryptoService.getCryptoPriceInUsd("nope")).thenReturn(new CurrentCryptoResponse(Map.of()));

        ResponseEntity<Map<String, Object>> response = controller.getCryptoPrice("nope");

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Price not found for cryptocurrency: nope", response.getBody().get("error"));
    }

    @Test
    void getCryptoPrice_ioError_returns500() throws Exception {
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenThrow(new IOException("down"));

        ResponseEntity<Map<String, Object>> response = controller.getCryptoPrice("bitcoin");

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error fetching price: down", response.getBody().get("error"));
    }

    @Test
    void getCryptoPrice_interrupted_returns500AndKeepsInterruptFlag() throws Exception {
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenThrow(new InterruptedException("stop"));

        ResponseEntity<Map<String, Object>> response = controller.getCryptoPrice("bitcoin");

        assertEquals(500, response.getStatusCode().value());
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void getCryptoPrice_unexpectedError_returns400() throws Exception {
        when(externalCryptoService.getCryptoPriceInUsd("bitcoin")).thenThrow(new IllegalArgumentException("bad id"));

        ResponseEntity<Map<String, Object>> response = controller.getCryptoPrice("bitcoin");

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid cryptocurrency id: bad id", response.getBody().get("error"));
    }

    @Test
    void getHistoricalPrice_returnsPrice() throws Exception {
        when(externalCryptoService.getHistoricalPrice("bitcoin", "2025-01-01")).thenReturn(historical(42000.0));

        ResponseEntity<Map<String, Object>> response = controller.getHistoricalPrice("Bitcoin", "2025-01-01");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("2025-01-01", response.getBody().get("date"));
        assertEquals(42000.0, response.getBody().get("price"));
    }

    @Test
    void getHistoricalPrice_noPrice_returns404() throws Exception {
        when(externalCryptoService.getHistoricalPrice("bitcoin", "2025-01-01")).thenReturn(historical(null));

        ResponseEntity<Map<String, Object>> response = controller.getHistoricalPrice("bitcoin", "2025-01-01");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getHistoricalPrice_ioError_returns500() throws Exception {
        when(externalCryptoService.getHistoricalPrice("bitcoin", "bad")).thenThrow(new IOException("Invalid date format"));

        ResponseEntity<Map<String, Object>> response = controller.getHistoricalPrice("bitcoin", "bad");

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error fetching historical price: Invalid date format", response.getBody().get("error"));
    }

    @Test
    void getHistoricalPrice_interrupted_returns500AndKeepsInterruptFlag() throws Exception {
        when(externalCryptoService.getHistoricalPrice("bitcoin", "2025-01-01")).thenThrow(new InterruptedException("stop"));

        ResponseEntity<Map<String, Object>> response = controller.getHistoricalPrice("bitcoin", "2025-01-01");

        assertEquals(500, response.getStatusCode().value());
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void getHistoricalPrice_missingMarketData_returns400() throws Exception {
        when(externalCryptoService.getHistoricalPrice("bitcoin", "2025-01-01")).thenReturn(new HistoricalCryptoResponse());

        ResponseEntity<Map<String, Object>> response = controller.getHistoricalPrice("bitcoin", "2025-01-01");

        assertEquals(400, response.getStatusCode().value());
    }
}

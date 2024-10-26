package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.service.CryptoService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final String COINGECKO_API_BASE_URL = "https://api.coingecko.com/api/v3";

    @Override
    public CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws IOException, InterruptedException {
        String url = COINGECKO_API_BASE_URL + "/simple/price?ids=" + cryptoId + "&vs_currencies=usd";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch price from CoinGecko API");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), CurrentCryptoResponse.class);
    }

    @Override
    public HistoricalCryptoResponse getHistoricalPrice(String cryptoId, String date) throws IOException, InterruptedException {
        String formattedDate = formatDate(date);
        String url = COINGECKO_API_BASE_URL + "/coins/" + cryptoId + "/history?date=" + formattedDate;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                //.header("x-cg-demo-api-key", "YOUR_API_KEY")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch historical price from CoinGecko API");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), HistoricalCryptoResponse.class);
    }

    private String formatDate(String date) throws IOException {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return localDate.format(formatter);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IOException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
    }
}

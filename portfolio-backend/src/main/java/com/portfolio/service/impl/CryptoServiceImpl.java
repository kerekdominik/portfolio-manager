package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.service.CryptoService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/simple/price";

    @Override
    public CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws IOException, InterruptedException {
        String url = COINGECKO_API_URL + "?ids=" + cryptoId + "&vs_currencies=usd";

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
        CurrentCryptoResponse currentCryptoResponse = objectMapper.readValue(
                response.body(),
                CurrentCryptoResponse.class
        );

        if (!currentCryptoResponse.getCryptoAndPrice().containsKey(cryptoId)) {
            throw new IOException("Price not found for cryptocurrency: " + cryptoId);
        }

        return currentCryptoResponse;
    }
}

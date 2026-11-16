package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.StockListItemRepository;
import com.portfolio.service.ExternalStockService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExternalStockServiceImpl implements ExternalStockService {

    private static final String ALPHA_VANTAGE_QUERY_URL = "https://www.alphavantage.co/query";

    private final StockListItemRepository stockListItemRepository;
    private final HttpClient client;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ExternalStockServiceImpl(StockListItemRepository stockListItemRepository,
                                    @Value("${alphavantage.api.key:}") String apiKey) {
        this(stockListItemRepository, HttpClient.newHttpClient(), apiKey);
    }

    ExternalStockServiceImpl(StockListItemRepository stockListItemRepository, HttpClient client, String apiKey) {
        this.stockListItemRepository = stockListItemRepository;
        this.client = client;
        this.apiKey = apiKey;
    }

    @PostConstruct
    @Override
    public List<StockListItem> fetchAllStocks() throws IOException, InterruptedException {
        if (!hasApiKey()) {
            log.warn("ALPHAVANTAGE_API_KEY is not set, skipping the stock list import");
            return List.of();
        }

        // CSV columns: symbol, name, exchange, assetType, ipoDate, delistingDate, status
        String[] lines = get(queryUrl("LISTING_STATUS", null)).split("\\R");

        List<StockListItem> stockList = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) { // index 0 is the header row
            String[] data = lines[i].split(",");
            if (data.length >= 7) {
                stockList.add(new StockListItem(data[0], data[1], data[2], data[3], data[6]));
            }
        }

        return stockListItemRepository.saveAll(stockList);
    }

    @Override
    public Double getCurrentStockPriceInUSD(String symbol) throws IOException, InterruptedException {
        if (!hasApiKey()) {
            throw new ExternalApiException("Alpha Vantage API key is not configured (set ALPHAVANTAGE_API_KEY)");
        }

        JsonNode globalQuoteNode = objectMapper.readTree(get(queryUrl("GLOBAL_QUOTE", symbol))).path("Global Quote");

        if (globalQuoteNode.isMissingNode()) {
            throw new ExternalApiException("No data found for symbol: " + symbol);
        }

        return Double.parseDouble(globalQuoteNode.path("05. price").asText());
    }

    private boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    private String queryUrl(String function, String symbol) {
        StringBuilder url = new StringBuilder(ALPHA_VANTAGE_QUERY_URL).append("?function=").append(function);
        if (symbol != null) {
            url.append("&symbol=").append(URLEncoder.encode(symbol, StandardCharsets.UTF_8));
        }
        return url.append("&apikey=").append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8)).toString();
    }

    private String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            // never log the URL: it contains the API key
            throw new IOException("Alpha Vantage request failed with HTTP status " + response.statusCode());
        }
        return response.body();
    }
}

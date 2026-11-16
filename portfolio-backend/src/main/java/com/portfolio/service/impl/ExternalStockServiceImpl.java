package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.StockListItemRepository;
import com.portfolio.service.ExternalStockService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
public class ExternalStockServiceImpl implements ExternalStockService {

    private static final String API_KEY = "P5WLC9QL7WQK2ABZ"; //TODO
    private static final String LISTING_STATUS_URL = "https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=" + API_KEY;
    private static final String GLOBAL_QUOTE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=" + API_KEY;

    private final StockListItemRepository stockListItemRepository;
    private final HttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ExternalStockServiceImpl(StockListItemRepository stockListItemRepository) {
        this(stockListItemRepository, HttpClient.newHttpClient());
    }

    ExternalStockServiceImpl(StockListItemRepository stockListItemRepository, HttpClient client) {
        this.stockListItemRepository = stockListItemRepository;
        this.client = client;
    }

    @PostConstruct
    @Override
    public List<StockListItem> fetchAllStocks() throws IOException, InterruptedException {
        // CSV columns: symbol, name, exchange, assetType, ipoDate, delistingDate, status
        String[] lines = get(LISTING_STATUS_URL).split("\\R");

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
        JsonNode globalQuoteNode = objectMapper.readTree(get(String.format(GLOBAL_QUOTE_URL, symbol))).path("Global Quote");

        if (globalQuoteNode.isMissingNode()) {
            throw new ExternalApiException("No data found for symbol: " + symbol);
        }

        return Double.parseDouble(globalQuoteNode.path("05. price").asText());
    }

    private String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Alpha Vantage request failed with HTTP status " + response.statusCode());
        }
        return response.body();
    }
}

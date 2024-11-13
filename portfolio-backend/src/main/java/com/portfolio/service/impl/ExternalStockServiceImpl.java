package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.repository.StockListItemRepository;
import com.portfolio.service.ExternalStockService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExternalStockServiceImpl implements ExternalStockService {

    private final StockListItemRepository stockListItemRepository;
    private static final String API_KEY = "P5WLC9QL7WQK2ABZ"; //TODO
    private static final String LISTING_STATUS_URL = "https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=" + API_KEY;
    private static final String GLOBAL_QUOTE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=" + API_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @PostConstruct
    public List<StockListItem> fetchAllStocks() throws Exception {
        URL url = new URL(LISTING_STATUS_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        List<StockListItem> stockList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header row
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 7) {
                    String symbol = data[0];
                    String name = data[1];
                    String exchange = data[2];
                    String assetType = data[3];
                    // LocalDate ipoDate = parseDate(data[4], formatter);
                    // LocalDate delistingDate = parseDate(data[5], formatter);
                    String status = data[6];

                    StockListItem stockListItem = new StockListItem(symbol, name, exchange, assetType, status);
                    stockList.add(stockListItem);
                }
            }
        }

        return stockListItemRepository.saveAll(stockList);
    }

    public Double getCurrentStockPriceInUSD(String symbol) throws Exception {
        String urlStr = String.format(GLOBAL_QUOTE_URL, symbol);
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            JsonNode rootNode = objectMapper.readTree(reader);
            JsonNode globalQuoteNode = rootNode.path("Global Quote");

            if (globalQuoteNode.isMissingNode()) {
                throw new RuntimeException("No data found for symbol: " + symbol);
            }

            String priceStr = globalQuoteNode.path("05. price").asText();
            return Double.parseDouble(priceStr);
        }
    }

    private LocalDate parseDate(String date, DateTimeFormatter formatter) {
        return (date == null || date.equals("null") || date.isEmpty()) ? null : LocalDate.parse(date, formatter);
    }
}
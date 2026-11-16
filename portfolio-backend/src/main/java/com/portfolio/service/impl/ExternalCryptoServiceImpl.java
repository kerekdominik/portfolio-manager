package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dto.CryptoListItemResponseDto;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.dto.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.dto.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.service.ExternalCryptoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ExternalCryptoServiceImpl implements ExternalCryptoService {

    private static final String COINGECKO_API_BASE_URL = "https://api.coingecko.com/api/v3";
    private static final DateTimeFormatter COINGECKO_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final CryptoListRepository cryptoListRepository;
    private final HttpClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ExternalCryptoServiceImpl(CryptoListRepository cryptoListRepository) {
        this(cryptoListRepository, HttpClient.newHttpClient());
    }

    ExternalCryptoServiceImpl(CryptoListRepository cryptoListRepository, HttpClient client) {
        this.cryptoListRepository = cryptoListRepository;
        this.client = client;
    }

    @PostConstruct
    public void fetchAllCryptosAtStartup() throws IOException, InterruptedException {
        HttpResponse<String> response = get(COINGECKO_API_BASE_URL + "/coins/list");

        if (response.statusCode() == 200) {
            List<CryptoListItemResponseDto> cryptoList = objectMapper.readValue(
                    response.body(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CryptoListItemResponseDto.class)
            );

            cryptoListRepository.deleteAll();

            List<CryptoListItem> cryptoCurrencies = cryptoList.stream()
                    .map(dto -> {
                        CryptoListItem cryptoCurrency = new CryptoListItem();
                        cryptoCurrency.setId(dto.getId());
                        cryptoCurrency.setSymbol(dto.getSymbol());
                        cryptoCurrency.setName(dto.getName());
                        return cryptoCurrency;
                    })
                    .toList();

            cryptoListRepository.saveAll(cryptoCurrencies);
        }
    }

    @Override
    @Cacheable(value = "cryptoPriceCache", key = "#cryptoId")
    public CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws IOException, InterruptedException {
        HttpResponse<String> response = get(COINGECKO_API_BASE_URL + "/simple/price?ids=" + cryptoId + "&vs_currencies=usd");

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch price from CoinGecko API");
        }

        return objectMapper.readValue(response.body(), CurrentCryptoResponse.class);
    }

    @Override
    @Cacheable(value = "historicalCryptoPriceCache", key = "#cryptoId + '-' + #date")
    public HistoricalCryptoResponse getHistoricalPrice(String cryptoId, String date) throws IOException, InterruptedException {
        String formattedDate = formatDate(date);
        HttpResponse<String> response = get(COINGECKO_API_BASE_URL + "/coins/" + cryptoId + "/history?date=" + formattedDate);

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch historical price from CoinGecko API");
        }

        return objectMapper.readValue(response.body(), HistoricalCryptoResponse.class);
    }

    private HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String formatDate(String date) throws IOException {
        try {
            return LocalDate.parse(date).format(COINGECKO_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid date format. Please use 'yyyy-MM-dd'.", e);
        }
    }
}

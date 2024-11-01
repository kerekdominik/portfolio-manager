package com.portfolio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dto.CryptoListItemResponseDto;
import com.portfolio.entity.asset.Crypto;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.repository.CryptoListRepository;
import com.portfolio.repository.CryptoRepository;
import com.portfolio.service.CryptoService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoListRepository cryptoListRepository;
    private static final String COINGECKO_API_BASE_URL = "https://api.coingecko.com/api/v3";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void fetchAllCryptosAtStartup() throws IOException, InterruptedException {
        String url = COINGECKO_API_BASE_URL + "/coins/list";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
                    .collect(Collectors.toList());

            cryptoListRepository.saveAll(cryptoCurrencies);
        }
    }

    @Override
    @Cacheable(value = "cryptoPriceCache", key = "#cryptoId")
    public CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws IOException, InterruptedException {
        String url = COINGECKO_API_BASE_URL + "/simple/price?ids=" + cryptoId + "&vs_currencies=usd";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch price from CoinGecko API");
        }

        return objectMapper.readValue(response.body(), CurrentCryptoResponse.class);
    }

    @Override
    @Cacheable(value = "historicalCryptoPriceCache", key = "#cryptoId + '-' + #date")
    public HistoricalCryptoResponse getHistoricalPrice(String cryptoId, String date) throws IOException, InterruptedException {
        String formattedDate = formatDate(date);
        String url = COINGECKO_API_BASE_URL + "/coins/" + cryptoId + "/history?date=" + formattedDate;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch historical price from CoinGecko API");
        }

        return objectMapper.readValue(response.body(), HistoricalCryptoResponse.class);
    }

    @Override
    public List<Crypto> getCryptosByUserId(Long userId) {
        return cryptoRepository.findAllById(userId);
    }

    @Override
    public Optional<Crypto> getCryptoById(Long id) {
        return cryptoRepository.findById(id);
    }

    @Override
    public Crypto saveCrypto(Crypto crypto) {
        return cryptoRepository.save(crypto);
    }

    @Override
    public Crypto updateCrypto(Long id, Crypto crypto) {
        return cryptoRepository.findById(id)
                .map(existingCrypto -> {
                    existingCrypto.setName(crypto.getName());
                    existingCrypto.setPriceWhenBought(crypto.getPriceWhenBought());
                    existingCrypto.setPriceNow(crypto.getPriceNow());
                    existingCrypto.setQuantity(crypto.getQuantity());
                    existingCrypto.setGroup(crypto.getGroup());
                    return cryptoRepository.save(existingCrypto);
                })
                .orElseThrow(() -> new RuntimeException("Crypto not found with id: " + id));
    }

    @Override
    public void deleteCrypto(Long id) {
        cryptoRepository.deleteById(id);
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

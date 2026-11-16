package com.portfolio.service.impl;

import com.portfolio.dto.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.dto.external.api.crypto.HistoricalCryptoResponse;
import com.portfolio.entity.asset.external.CryptoListItem;
import com.portfolio.repository.CryptoListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalCryptoServiceImplTest {

    @Mock
    private CryptoListRepository cryptoListRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ExternalCryptoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ExternalCryptoServiceImpl(cryptoListRepository, httpClient);
    }

    private void givenResponse(int status, String body) throws Exception {
        doReturn(httpResponse).when(httpClient).send(any(HttpRequest.class), any());
        when(httpResponse.statusCode()).thenReturn(status);
        if (status == 200) {
            when(httpResponse.body()).thenReturn(body);
        }
    }

    private HttpRequest sentRequest() throws Exception {
        ArgumentCaptor<HttpRequest> request = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(request.capture(), any());
        return request.getValue();
    }

    @Test
    void fetchAllCryptosAtStartup_replacesStoredList() throws Exception {
        givenResponse(200, """
                [{"id":"bitcoin","symbol":"btc","name":"Bitcoin"},
                 {"id":"ethereum","symbol":"eth","name":"Ethereum"}]""");

        service.fetchAllCryptosAtStartup();

        verify(cryptoListRepository).deleteAll();
        verify(cryptoListRepository).saveAll(List.of(
                new CryptoListItem("bitcoin", "btc", "Bitcoin"),
                new CryptoListItem("ethereum", "eth", "Ethereum")));
        HttpRequest request = sentRequest();
        assertEquals("https://api.coingecko.com/api/v3/coins/list", request.uri().toString());
        assertEquals(List.of("application/json"), request.headers().allValues("accept"));
    }

    @Test
    void fetchAllCryptosAtStartup_errorStatus_keepsStoredList() throws Exception {
        givenResponse(429, null);

        service.fetchAllCryptosAtStartup();

        verifyNoInteractions(cryptoListRepository);
    }

    @Test
    void getCryptoPriceInUsd_parsesPrice() throws Exception {
        givenResponse(200, "{\"bitcoin\":{\"usd\":50123.5}}");

        CurrentCryptoResponse response = service.getCryptoPriceInUsd("bitcoin");

        assertEquals(50123.5, response.getPriceInUSD("bitcoin"));
        assertNull(response.getPriceInUSD("ethereum"));
        assertEquals("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd",
                sentRequest().uri().toString());
    }

    @Test
    void getCryptoPriceInUsd_errorStatus_throws() throws Exception {
        givenResponse(500, null);

        IOException exception = assertThrows(IOException.class, () -> service.getCryptoPriceInUsd("bitcoin"));
        assertEquals("Failed to fetch price from CoinGecko API", exception.getMessage());
    }

    @Test
    void getHistoricalPrice_usesCoinGeckoDateFormat() throws Exception {
        givenResponse(200, """
                {"id":"bitcoin","symbol":"btc","name":"Bitcoin",
                 "market_data":{"current_price":{"usd":42000.0,"eur":39000.0}}}""");

        HistoricalCryptoResponse response = service.getHistoricalPrice("bitcoin", "2025-01-15");

        assertEquals(42000.0, response.getMarketData().getPriceInUSD());
        assertEquals(39000.0, response.getMarketData().getPriceInCurrency("EUR"));
        assertNull(response.getMarketData().getPriceInCurrency("huf"));
        assertEquals("https://api.coingecko.com/api/v3/coins/bitcoin/history?date=15-01-2025",
                sentRequest().uri().toString());
    }

    @Test
    void getHistoricalPrice_invalidDate_throwsWithoutCallingApi() {
        IOException exception = assertThrows(IOException.class, () -> service.getHistoricalPrice("bitcoin", "15/01/2025"));

        assertEquals("Invalid date format. Please use 'yyyy-MM-dd'.", exception.getMessage());
        verifyNoInteractions(httpClient);
    }

    @Test
    void getHistoricalPrice_errorStatus_throws() throws Exception {
        givenResponse(404, null);

        IOException exception = assertThrows(IOException.class, () -> service.getHistoricalPrice("bitcoin", "2025-01-15"));
        assertEquals("Failed to fetch historical price from CoinGecko API", exception.getMessage());
    }

    @Test
    void springConstructor_createsDefaultHttpClient() {
        assertNotNull(new ExternalCryptoServiceImpl(cryptoListRepository));
    }
}

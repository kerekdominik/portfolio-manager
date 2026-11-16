package com.portfolio.service.impl;

import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.StockListItemRepository;
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
class ExternalStockServiceImplTest {

    private static final String TEST_KEY = "test-key";

    @Mock
    private StockListItemRepository stockListItemRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ExternalStockServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ExternalStockServiceImpl(stockListItemRepository, httpClient, TEST_KEY);
    }

    private void givenResponse(int status, String body) throws Exception {
        doReturn(httpResponse).when(httpClient).send(any(HttpRequest.class), any());
        when(httpResponse.statusCode()).thenReturn(status);
        if (status == 200) {
            when(httpResponse.body()).thenReturn(body);
        }
    }

    private String sentUrl() throws Exception {
        ArgumentCaptor<HttpRequest> request = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(request.capture(), any());
        return request.getValue().uri().toString();
    }

    @Test
    void fetchAllStocks_parsesCsvAndSkipsHeaderAndIncompleteRows() throws Exception {
        givenResponse(200, """
                symbol,name,exchange,assetType,ipoDate,delistingDate,status
                AAPL,Apple Inc,NASDAQ,Stock,1980-12-12,null,Active
                BROKEN,row
                MSFT,Microsoft Corporation,NASDAQ,Stock,1986-03-13,null,Active
                """);
        List<StockListItem> expected = List.of(
                new StockListItem("AAPL", "Apple Inc", "NASDAQ", "Stock", "Active"),
                new StockListItem("MSFT", "Microsoft Corporation", "NASDAQ", "Stock", "Active"));
        when(stockListItemRepository.saveAll(expected)).thenReturn(expected);

        assertEquals(expected, service.fetchAllStocks());
        assertEquals("https://www.alphavantage.co/query?function=LISTING_STATUS&apikey=test-key", sentUrl());
    }

    @Test
    void fetchAllStocks_errorStatus_throwsWithoutLeakingKey() throws Exception {
        givenResponse(503, null);

        IOException exception = assertThrows(IOException.class, () -> service.fetchAllStocks());
        assertEquals("Alpha Vantage request failed with HTTP status 503", exception.getMessage());
        assertFalse(exception.getMessage().contains(TEST_KEY));
        verifyNoInteractions(stockListItemRepository);
    }

    @Test
    void fetchAllStocks_withoutApiKey_skipsImport() throws Exception {
        for (String missing : new String[]{null, "", "   "}) {
            ExternalStockServiceImpl withoutKey = new ExternalStockServiceImpl(stockListItemRepository, httpClient, missing);

            assertTrue(withoutKey.fetchAllStocks().isEmpty());
        }
        verifyNoInteractions(httpClient, stockListItemRepository);
    }

    @Test
    void getCurrentStockPriceInUSD_parsesGlobalQuote() throws Exception {
        givenResponse(200, "{\"Global Quote\":{\"01. symbol\":\"AAPL\",\"05. price\":\"189.9800\"}}");

        assertEquals(189.98, service.getCurrentStockPriceInUSD("AAPL"));
        assertEquals("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=AAPL&apikey=test-key", sentUrl());
    }

    @Test
    void getCurrentStockPriceInUSD_encodesSymbol() throws Exception {
        givenResponse(200, "{\"Global Quote\":{\"05. price\":\"10.0\"}}");

        service.getCurrentStockPriceInUSD("BRK.B&x=1");

        assertEquals("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=BRK.B%26x%3D1&apikey=test-key", sentUrl());
    }

    @Test
    void getCurrentStockPriceInUSD_missingQuote_throws() throws Exception {
        givenResponse(200, "{\"Information\":\"rate limit reached\"}");

        ExternalApiException exception = assertThrows(ExternalApiException.class,
                () -> service.getCurrentStockPriceInUSD("AAPL"));
        assertEquals("No data found for symbol: AAPL", exception.getMessage());
    }

    @Test
    void getCurrentStockPriceInUSD_withoutApiKey_throwsClearError() {
        ExternalStockServiceImpl withoutKey = new ExternalStockServiceImpl(stockListItemRepository, httpClient, "");

        ExternalApiException exception = assertThrows(ExternalApiException.class,
                () -> withoutKey.getCurrentStockPriceInUSD("AAPL"));
        assertTrue(exception.getMessage().contains("ALPHAVANTAGE_API_KEY"));
        verifyNoInteractions(httpClient);
    }

    @Test
    void springConstructor_createsDefaultHttpClient() {
        assertNotNull(new ExternalStockServiceImpl(stockListItemRepository, TEST_KEY));
    }
}

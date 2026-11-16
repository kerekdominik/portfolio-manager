package com.portfolio.service.impl;

import com.portfolio.entity.asset.external.StockListItem;
import com.portfolio.exception.ExternalApiException;
import com.portfolio.repository.StockListItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private StockListItemRepository stockListItemRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ExternalStockServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ExternalStockServiceImpl(stockListItemRepository, httpClient);
    }

    private void givenResponse(int status, String body) throws Exception {
        doReturn(httpResponse).when(httpClient).send(any(HttpRequest.class), any());
        when(httpResponse.statusCode()).thenReturn(status);
        if (status == 200) {
            when(httpResponse.body()).thenReturn(body);
        }
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
    }

    @Test
    void fetchAllStocks_errorStatus_throws() throws Exception {
        givenResponse(503, null);

        IOException exception = assertThrows(IOException.class, () -> service.fetchAllStocks());
        assertEquals("Alpha Vantage request failed with HTTP status 503", exception.getMessage());
        verifyNoInteractions(stockListItemRepository);
    }

    @Test
    void getCurrentStockPriceInUSD_parsesGlobalQuote() throws Exception {
        givenResponse(200, "{\"Global Quote\":{\"01. symbol\":\"AAPL\",\"05. price\":\"189.9800\"}}");

        assertEquals(189.98, service.getCurrentStockPriceInUSD("AAPL"));
    }

    @Test
    void getCurrentStockPriceInUSD_missingQuote_throws() throws Exception {
        givenResponse(200, "{\"Information\":\"rate limit reached\"}");

        ExternalApiException exception = assertThrows(ExternalApiException.class,
                () -> service.getCurrentStockPriceInUSD("AAPL"));
        assertEquals("No data found for symbol: AAPL", exception.getMessage());
    }

    @Test
    void springConstructor_createsDefaultHttpClient() {
        assertNotNull(new ExternalStockServiceImpl(stockListItemRepository));
    }
}

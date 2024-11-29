package com.portfolio.service.impl;

import com.portfolio.entity.asset.Stock;
import com.portfolio.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    void testSaveStock() {
        // Arrange
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");

        // Act
        stockService.saveStock(stock);

        // Assert
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void testGetStockById_Found() {
        // Arrange
        Long stockId = 1L;
        Stock stock = new Stock("AAPL", "Apple Inc.", "NASDAQ");
        stock.setId(stockId);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));

        // Act
        Optional<Stock> result = stockService.getStockById(stockId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(stock, result.get());
        verify(stockRepository, times(1)).findById(stockId);
    }

    @Test
    void testGetStockById_NotFound() {
        // Arrange
        Long stockId = 1L;
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        // Act
        Optional<Stock> result = stockService.getStockById(stockId);

        // Assert
        assertFalse(result.isPresent());
        verify(stockRepository, times(1)).findById(stockId);
    }

    @Test
    void testGetAllStocks() {
        // Arrange
        Stock stock1 = new Stock("AAPL", "Apple Inc.", "NASDAQ");
        Stock stock2 = new Stock("GOOGL", "Alphabet Inc.", "NASDAQ");
        List<Stock> stockList = Arrays.asList(stock1, stock2);
        when(stockRepository.findAll()).thenReturn(stockList);

        // Act
        List<Stock> result = stockService.getAllStocks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(stockList, result);
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void testDeleteStock() {
        // Arrange
        Long stockId = 1L;

        // Act
        stockService.deleteStock(stockId);

        // Assert
        verify(stockRepository, times(1)).deleteById(stockId);
    }
}

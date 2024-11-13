package com.portfolio.service;

import com.portfolio.entity.asset.Stock;
import com.portfolio.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public void saveStock(Stock stock) {
        stockRepository.save(stock);
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }
}

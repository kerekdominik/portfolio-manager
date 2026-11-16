package com.portfolio.service;

import com.portfolio.entity.asset.external.StockListItem;

import java.io.IOException;
import java.util.List;

public interface ExternalStockService {
    List<StockListItem> fetchAllStocks() throws IOException, InterruptedException;
    Double getCurrentStockPriceInUSD(String symbol) throws IOException, InterruptedException;
}

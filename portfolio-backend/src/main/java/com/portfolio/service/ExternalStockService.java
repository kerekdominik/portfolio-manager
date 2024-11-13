package com.portfolio.service;

import com.portfolio.entity.asset.external.StockListItem;

import java.util.List;

public interface ExternalStockService {
    List<StockListItem> fetchAllStocks() throws Exception;
    Double getCurrentStockPriceInUSD(String symbol) throws Exception;
}
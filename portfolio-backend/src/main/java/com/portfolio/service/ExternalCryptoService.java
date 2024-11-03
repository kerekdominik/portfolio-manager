package com.portfolio.service;

import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;

public interface ExternalCryptoService {
    CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws Exception;
    HistoricalCryptoResponse getHistoricalPrice(String cryptoId, String date) throws Exception;
}

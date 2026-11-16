package com.portfolio.service;

import com.portfolio.dto.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.dto.external.api.crypto.HistoricalCryptoResponse;

import java.io.IOException;

public interface ExternalCryptoService {
    CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws IOException, InterruptedException;
    HistoricalCryptoResponse getHistoricalPrice(String cryptoId, String date) throws IOException, InterruptedException;
}

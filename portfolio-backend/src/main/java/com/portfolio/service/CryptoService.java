package com.portfolio.service;

import com.portfolio.external.api.crypto.CurrentCryptoResponse;

public interface CryptoService {
    CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws Exception;
}

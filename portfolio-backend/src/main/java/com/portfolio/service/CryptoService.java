package com.portfolio.service;

import com.portfolio.entity.asset.Crypto;
import com.portfolio.external.api.crypto.CurrentCryptoResponse;
import com.portfolio.external.api.crypto.HistoricalCryptoResponse;

import java.util.List;
import java.util.Optional;

public interface CryptoService {
    CurrentCryptoResponse getCryptoPriceInUsd(String cryptoId) throws Exception;
    HistoricalCryptoResponse getHistoricalPrice(String cryptoId, String date) throws Exception;

    List<Crypto> getCryptosByUserId(Long userId);
    Optional<Crypto> getCryptoById(Long id);
    Crypto saveCrypto(Crypto crypto);
    Crypto updateCrypto(Long id, Crypto crypto);
    void deleteCrypto(Long id);
}

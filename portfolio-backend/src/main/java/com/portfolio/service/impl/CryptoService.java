package com.portfolio.service.impl;

import com.portfolio.entity.asset.Crypto;
import com.portfolio.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoRepository cryptoRepository;

    public void saveCrypto(Crypto crypto) {
        cryptoRepository.save(crypto);
    }

    public Optional<Crypto> getCryptoById(Long id) {
        return cryptoRepository.findById(id);
    }

    public Optional<Crypto> getCryptoByExternalId(String id) {
        return cryptoRepository.findByExternalId(id);
    }

    public List<Crypto> getCryptosByUserId(Long userId) {
        return cryptoRepository.findAllById(userId);
    }

    public void deleteCrypto(Long id) {
        cryptoRepository.deleteById(id);
    }
}

package com.portfolio.service.impl;

import com.portfolio.entity.asset.Crypto;
import com.portfolio.repository.CryptoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptoServiceTest {

    @Mock
    private CryptoRepository cryptoRepository;

    @InjectMocks
    private CryptoService cryptoService;

    @Test
    void testSaveCrypto() {
        // Arrange
        Crypto crypto = new Crypto();
        crypto.setId(1L);
        crypto.setExternalId("crypto-123");

        // Act
        cryptoService.saveCrypto(crypto);

        // Assert
        verify(cryptoRepository).save(crypto);
    }

    @Test
    void testGetCryptoById_Found() {
        // Arrange
        Long id = 1L;
        Crypto crypto = new Crypto();
        crypto.setId(id);
        when(cryptoRepository.findById(id)).thenReturn(Optional.of(crypto));

        // Act
        Optional<Crypto> result = cryptoService.getCryptoById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(crypto, result.get());
        verify(cryptoRepository).findById(id);
    }

    @Test
    void testGetCryptoById_NotFound() {
        // Arrange
        Long id = 1L;
        when(cryptoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Crypto> result = cryptoService.getCryptoById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(cryptoRepository).findById(id);
    }

    @Test
    void testGetCryptoByExternalId_Found() {
        // Arrange
        String externalId = "crypto-123";
        Crypto crypto = new Crypto();
        crypto.setExternalId(externalId);
        when(cryptoRepository.findByExternalId(externalId)).thenReturn(Optional.of(crypto));

        // Act
        Optional<Crypto> result = cryptoService.getCryptoByExternalId(externalId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(crypto, result.get());
        verify(cryptoRepository).findByExternalId(externalId);
    }

    @Test
    void testGetCryptoByExternalId_NotFound() {
        // Arrange
        String externalId = "crypto-123";
        when(cryptoRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        // Act
        Optional<Crypto> result = cryptoService.getCryptoByExternalId(externalId);

        // Assert
        assertFalse(result.isPresent());
        verify(cryptoRepository).findByExternalId(externalId);
    }

    @Test
    void testGetCryptosByUserId() {
        // Arrange
        Long userId = 1L;
        List<Crypto> cryptos = Arrays.asList(new Crypto(), new Crypto());
        when(cryptoRepository.findAllById(userId)).thenReturn(cryptos);

        // Act
        List<Crypto> result = cryptoService.getCryptosByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(cryptos.size(), result.size());
        verify(cryptoRepository).findAllById(userId);
    }

    @Test
    void testDeleteCrypto() {
        // Arrange
        Long id = 1L;

        // Act
        cryptoService.deleteCrypto(id);

        // Assert
        verify(cryptoRepository).deleteById(id);
    }

    @Test
    void testDeleteCrypto_VerifyNoMoreInteractions() {
        // Arrange
        Long id = 1L;

        // Act
        cryptoService.deleteCrypto(id);

        // Assert
        verify(cryptoRepository).deleteById(id);
        verifyNoMoreInteractions(cryptoRepository);
    }
}

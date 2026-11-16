package com.portfolio.exception;

/**
 * Thrown when an external market data API (CoinGecko, Alpha Vantage) cannot be reached
 * or returns data the application cannot use.
 */
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.portfolio.external.api.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentCryptoResponse {

    private final Map<String, Map<String, Double>> cryptoAndPrice;

    @JsonCreator
    public CurrentCryptoResponse(Map<String, Map<String, Double>> cryptoAndPrice) {
        this.cryptoAndPrice = cryptoAndPrice;
    }

    public Map<String, Map<String, Double>> getCryptoAndPrice() {
        return cryptoAndPrice;
    }

    public Double getPriceInUSD(String cryptoSymbol) {
        if (cryptoAndPrice != null && cryptoAndPrice.containsKey(cryptoSymbol)) {
            Map<String, Double> prices = cryptoAndPrice.get(cryptoSymbol);
            if (prices != null && prices.containsKey("usd")) {
                return prices.get("usd");
            }
        }
        return null;
    }
}

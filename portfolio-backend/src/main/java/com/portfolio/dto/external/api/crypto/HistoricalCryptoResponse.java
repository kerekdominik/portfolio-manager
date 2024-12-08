package com.portfolio.dto.external.api.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalCryptoResponse {
    private String id;
    private String symbol;
    private String name;
    private MarketData marketData;

    @JsonProperty("market_data")
    public MarketData getMarketData() { return marketData; }

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MarketData {

        @JsonProperty("current_price")
        private Map<String, Double> currentPrice;

        public Double getPriceInUSD() {
            if (currentPrice != null && currentPrice.containsKey("usd")) {
                return currentPrice.get("usd");
            }
            return null;
        }

        public Double getPriceInCurrency(String currency) {
            currency = currency.toLowerCase();
            if (currentPrice != null && currentPrice.containsKey(currency)) {
                return currentPrice.get(currency);
            }
            return null;
        }
    }
}

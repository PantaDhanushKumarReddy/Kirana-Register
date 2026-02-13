package com.example.Kirana.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class CurrencyRateDao {

    private final RestClient restClient;

    public CurrencyRateDao(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Fetch FX rates for a base currency.
     * Cached to avoid hitting API limits.
     */
    @Cacheable(
            value = "fx-rates",
            key = "#baseCurrency"
    )
    public Map<String, Object> getRates(String baseCurrency) {

        String url =
                "https://api.fxratesapi.com/latest?base=" + baseCurrency;

        Map<String, Object> response =
                restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(Map.class);

        if (response == null || !response.containsKey("rates")) {
            throw new RuntimeException("FX rate service unavailable");
        }

        return (Map<String, Object>) response.get("rates");
    }
}

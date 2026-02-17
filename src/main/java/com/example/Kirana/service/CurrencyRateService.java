package com.example.Kirana.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class CurrencyRateService {

    private final RestClient restClient;
    public CurrencyRateService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Fetch FX rates for a base currency.
     */
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
        System.out.println("I got call to DB");
        return (Map<String, Object>) response.get("rates");
    }
}

package com.example.Kirana.service;
import com.example.Kirana.dao.CurrencyRateDao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CurrencyService {

    private final CurrencyRateDao rateDao;

    public CurrencyService(CurrencyRateDao rateDao) {
        this.rateDao = rateDao;
    }

    public BigDecimal convert(BigDecimal amount,
                              String fromCurrency,
                              String toCurrency) {

        if (amount == null) return BigDecimal.ZERO;

        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();

        if (fromCurrency.equals(toCurrency)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        Map<String, Object> rates =
                rateDao.getRates(fromCurrency);

        if (!rates.containsKey(toCurrency)) {
            throw new RuntimeException(
                    "Currency not supported: " + toCurrency);
        }

        BigDecimal rate =
                new BigDecimal(rates.get(toCurrency).toString());

        return amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
}

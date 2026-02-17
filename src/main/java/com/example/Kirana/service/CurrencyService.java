package com.example.Kirana.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Map;

@Service
public class CurrencyService {

    private static final String REDIS_KEY_PREFIX ="fx:rates:" ;
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private final CurrencyRateService rateDao;
    private final StringRedisTemplate redisTemplate;
    /**
     * Redis cannot store Java objects directly.
     * We serialize Map<String, Object> → JSON and deserialize it back.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    public CurrencyService(CurrencyRateService rateDao, StringRedisTemplate redisTemplate) {

        this.rateDao = rateDao;
        this.redisTemplate = redisTemplate;
    }
    /**
     * Converts an amount from one currency to another.
     *
     * Conversion logic must be:
     *  - Accurate
     *  - Resilient
     *  - Non-blocking
     *
     * Even if caching does not work, conversion should still work.
     */
    public BigDecimal convert(BigDecimal amount,
                              String fromCurrency,
                              String toCurrency) {

        if (amount == null) return BigDecimal.ZERO;

        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();
        // No conversion needed
        if (fromCurrency.equals(toCurrency)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }
        // Get conversion rates for base currency
        Map<String, Object> rates =getRateFunction(fromCurrency);

        if (!rates.containsKey(toCurrency)) {
            throw new RuntimeException(
                    "Currency not supported: " + toCurrency);
        }

        BigDecimal rate =
                new BigDecimal(rates.get(toCurrency).toString());

        return amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
    /**
     * getRateFunction
     *
     * Flow:
     *  1. Try to read exchange rates from Redis (FAST)
     *  2. If Redis has valid data → use it
     *  3. If Redis doesn't work (down / corrupted data) → ignore error
     *  4. Fetch fresh rates from external API
     *  5. Try to cache the fresh rates back into Redis
     *
     * WHY TRY-CATCH IS USED HERE:
     * ---------------------------
     * Redis is an optimization, NOT a dependency.
     *
     * If we throw exceptions here:
     *  - Currency conversion would fail
     *  - Sales and refunds would fail
     *  - Entire transaction flow would break
     *
     * So we intentionally swallow Redis-related exceptions
     * and continue using live data.
     */
   public Map<String, Object> getRateFunction(String baseCurrency) {
       baseCurrency = baseCurrency.toUpperCase();
       String redisKey = REDIS_KEY_PREFIX + baseCurrency;
       String cachedJson = redisTemplate.opsForValue().get(redisKey);
       if (cachedJson != null) {
           try {
               System.out.println(cachedJson);
               return objectMapper.readValue(
                       cachedJson,
                       new TypeReference<Map<String, Object>>() {}
               );
               //Deserialize cached value
           } catch (Exception e) {
               /*
                * We DO NOT throw exception here because:
                *  - Cached data may be corrupted
                *  - Redis data format may have changed
                *  - ObjectMapper may not work due to schema mismatch
                *
                * In all cases, we simply fall back to the API.
                */
           }
       }
       Map<String, Object> rates =
               rateDao.getRates(baseCurrency);
       try {
           String json = objectMapper.writeValueAsString(rates);
           redisTemplate.opsForValue()
                   .set(redisKey, json, CACHE_TTL);
       } catch (Exception e) {
           // Because of Redis not working properly we must not break conversion
       }

       return rates;
   }
}

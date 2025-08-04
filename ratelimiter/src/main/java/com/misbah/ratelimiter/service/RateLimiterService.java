
package com.misbah.ratelimiter.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ValueOperations<String, String> redisValueOps;

    public RateLimiterService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisValueOps = stringRedisTemplate.opsForValue();
    }

    public boolean isAllowedFixedWindow(String apiKey, int limit, int windowInSeconds) {
        String key = "rate_limit:fixed_window:" + apiKey;
        Long currentRequests = redisValueOps.increment(key);

        if (currentRequests == null) {
            return false;
        }

        if (currentRequests == 1) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(windowInSeconds));
        }

        return currentRequests <= limit;
    }

    public boolean isAllowedTokenBucket(String apiKey, int capacity, double refillRate) {
        String tokensKey = "rate_limit:token_bucket:tokens:" + apiKey;
        String timestampKey = "rate_limit:token_bucket:timestamp:" + apiKey;

        refillBucket(tokensKey, timestampKey, capacity, refillRate);

        String currentTokensStr = redisValueOps.get(tokensKey);
        double currentTokens = (currentTokensStr != null) ? Double.parseDouble(currentTokensStr) : capacity;

        if (currentTokens >= 1) {
            double newTokens = currentTokens - 1;
            redisValueOps.set(tokensKey, String.valueOf(newTokens));
            return true;
        } else {
            return false;
        }
    }

    private void refillBucket(String tokensKey, String timestampKey, int capacity, double refillRate) {
        String lastRefillTimestampStr = redisValueOps.get(timestampKey);
        long lastRefillTimestamp = (lastRefillTimestampStr != null) ? Long.parseLong(lastRefillTimestampStr) : 0;
        long now = Instant.now().getEpochSecond();

        if (lastRefillTimestamp == 0) {
            redisValueOps.set(tokensKey, String.valueOf(capacity));
            redisValueOps.set(timestampKey, String.valueOf(now));
            return;
        }

        long timeElapsed = now - lastRefillTimestamp;
        double tokensToAdd = timeElapsed * refillRate;

        if (tokensToAdd > 0) {
            String currentTokensStr = redisValueOps.get(tokensKey);
            double currentTokens = (currentTokensStr != null) ? Double.parseDouble(currentTokensStr) : capacity;

            double newTokens = Math.min(capacity, currentTokens + tokensToAdd);

            redisValueOps.set(tokensKey, String.valueOf(newTokens));
            redisValueOps.set(timestampKey, String.valueOf(now));
        }
    }
}

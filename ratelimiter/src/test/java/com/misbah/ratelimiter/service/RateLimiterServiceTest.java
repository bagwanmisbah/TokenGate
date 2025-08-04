
package com.misbah.ratelimiter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        rateLimiterService = new RateLimiterService(stringRedisTemplate);
    }

    @Test
    void isAllowedFixedWindow_ShouldAllowRequestsWithinLimit() {

        String apiKey = "test-key";
        int limit = 5;
        int windowInSeconds = 60;
        String redisKey = "rate_limit:fixed_window:" + apiKey;

        when(valueOperations.increment(redisKey)).thenReturn(1L, 2L, 3L, 4L, 5L);

        for (int i = 0; i < limit; i++) {
            boolean allowed = rateLimiterService.isAllowedFixedWindow(apiKey, limit, windowInSeconds);
            assertTrue(allowed, "Request " + (i + 1) + " should be allowed.");
        }

        verify(stringRedisTemplate, times(1)).expire(redisKey, Duration.ofSeconds(windowInSeconds));
    }

    @Test
    void isAllowedFixedWindow_ShouldBlockRequestsExceedingLimit() {
        String apiKey = "test-key-2";
        int limit = 3;
        int windowInSeconds = 60;
        String redisKey = "rate_limit:fixed_window:" + apiKey;

        when(valueOperations.increment(redisKey)).thenReturn(1L, 2L, 3L, 4L);

        assertTrue(rateLimiterService.isAllowedFixedWindow(apiKey, limit, windowInSeconds));
        assertTrue(rateLimiterService.isAllowedFixedWindow(apiKey, limit, windowInSeconds));
        assertTrue(rateLimiterService.isAllowedFixedWindow(apiKey, limit, windowInSeconds));

        boolean fourthRequestAllowed = rateLimiterService.isAllowedFixedWindow(apiKey, limit, windowInSeconds);
        assertFalse(fourthRequestAllowed, "The 4th request should be blocked.");

        verify(valueOperations, times(4)).increment(redisKey);
    }
}

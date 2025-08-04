
package com.misbah.ratelimiter.interceptor;

import com.misbah.ratelimiter.exception.RateLimitExceededException;
import com.misbah.ratelimiter.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    public RateLimitingInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "User not authenticated");
            return false;
        }

        String username = authentication.getName();

        String requestPath = request.getRequestURI();
        boolean allowed;

        if (requestPath.startsWith("/api/v1/")) {
            allowed = rateLimiterService.isAllowedFixedWindow(username, 10, 60);

        } else if (requestPath.startsWith("/api/v2/")) {
            allowed = rateLimiterService.isAllowedTokenBucket(username, 15, 0.5);

        } else {
            allowed = true;
        }

        if (allowed) {
            return true;
        } else {

            throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
        }
    }
}
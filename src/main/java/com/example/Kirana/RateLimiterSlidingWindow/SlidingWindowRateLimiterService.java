package com.example.Kirana.RateLimiterSlidingWindow;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SlidingWindowRateLimiterService {
    private final Map<String, SlidingWindow> rateLimitWindows =
            new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SIZE = 60000; // 1 Minute=60,000 milliseconds
    public boolean isAllowed(String email) {
        SlidingWindow window = rateLimitWindows.computeIfAbsent(
                email.toLowerCase(),
                key -> new SlidingWindow(MAX_REQUESTS, WINDOW_SIZE)
        );
        return window.isAllowed();
    }
    public long getRemainingAttempts(String email) {
        SlidingWindow window = rateLimitWindows.get(email.toLowerCase());
        return window != null ? window.remainingRequests() : MAX_REQUESTS;
    }
}

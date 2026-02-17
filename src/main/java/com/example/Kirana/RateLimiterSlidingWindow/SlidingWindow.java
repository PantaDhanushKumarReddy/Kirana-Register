package com.example.Kirana.RateLimiterSlidingWindow;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

public class SlidingWindow {
    private final long maxRequests;
    private final long windowSizeMillis;
    private final Deque<Long> timestamps = new LinkedList<>();

    public SlidingWindow(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
    }
    public boolean isAllowed(){
        Date now = new Date();
        long nowMillis = now.getTime();
        // Remove timestamps outside window
        while(!timestamps.isEmpty()&&nowMillis-timestamps.peekFirst()>windowSizeMillis){
            timestamps.pollFirst();
        }
        if(timestamps.size()<maxRequests){
            timestamps.addLast(nowMillis);
            return true;
        }
        return false;
    }
    public long remainingRequests() {
        return Math.max(0, maxRequests - timestamps.size());
    }
}

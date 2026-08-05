package com.iot.dashboard.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class DataRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 30;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, long[]> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().endsWith("/data")) {
            String clientIp = request.getRemoteAddr();
            long now = System.currentTimeMillis();

            long[] counter = requestCounts.compute(clientIp, (ip, value) -> {
                if (value == null || now - value[0] > WINDOW_MS) {
                    return new long[]{now, 1};
                }
                value[1]++;
                return value;
            });

            if (counter[1] > MAX_REQUESTS) {
                response.setStatus(429);
                response.getWriter().write("Çok fazla istek gönderildi, lütfen daha sonra tekrar deneyin.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

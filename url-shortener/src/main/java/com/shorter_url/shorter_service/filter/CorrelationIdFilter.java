package com.shorter_url.shorter_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME =
            "X-Correlation-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {

            String correlationId = request.getHeader(HEADER_NAME);

            if(correlationId == null || correlationId.isBlank()) {

                correlationId = UUID.randomUUID().toString().substring(0,7);

            }

            MDC.put("correlationId", correlationId);
            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}

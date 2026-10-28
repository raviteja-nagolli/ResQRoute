package com.example.order_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.kafka.support.KafkaHeaders.CORRELATION_ID;

@Component
public class CorrelationIdLoggingFilter extends OncePerRequestFilter {
    private static final String CORRELATION_ID="X-Correlation-ID";

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException{
        String correlationId=httpServletRequest.getHeader(CORRELATION_ID);

        if (correlationId==null || correlationId.isBlank()){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        MDC.put(CORRELATION_ID,correlationId);
        try {
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}

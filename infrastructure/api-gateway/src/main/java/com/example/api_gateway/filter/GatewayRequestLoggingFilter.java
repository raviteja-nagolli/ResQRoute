package com.example.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

public class GatewayRequestLoggingFilter {
    private static final Logger log= LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);
    private static final String CORRELATION_ID="X-Correlation-ID";

    public static HandlerFilterFunction<ServerResponse, ServerResponse> logRequests(){
        return ((request, next) -> {
            long start=System.currentTimeMillis();
            String correlationId=request.headers().firstHeader(CORRELATION_ID);
            log.info("Incoming request: correlationId={}, method={}, path={}",correlationId, request.method(), request.path());
            try {
                ServerResponse response = next.handle(request);
                long duration = System.currentTimeMillis() - start;
                log.info("Request completed: correlationId={}, method={}, path={}, status={}, duration={}ms",correlationId , request.method(), request.path(), response.statusCode(), duration);
                return response;
            }catch (Exception e){
                long duration=System.currentTimeMillis()-start;
                log.error("Request failed: correlationId={}, method={}, path={}, duration={}ms",correlationId, request.method(), request.path(), duration);
                throw e;
            }
        });
    }
}

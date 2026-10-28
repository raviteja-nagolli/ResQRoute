package com.example.api_gateway.filter;

import org.springframework.web.servlet.function.ServerRequest;

import java.util.UUID;
import java.util.function.Function;

public class CorrelationIdFilter {
    private static final String CORRELATION_ID="X-Correlation-ID";
    public static Function<ServerRequest, ServerRequest> addCorrelationId(){
        return request -> {
            String correlationId=request.headers().firstHeader(CORRELATION_ID);
            if (correlationId==null || correlationId.isBlank()){
                correlationId= UUID.randomUUID().toString();
            }
            return ServerRequest.from(request)
                    .header(CORRELATION_ID,correlationId)
                    .build();
        };
    }
}

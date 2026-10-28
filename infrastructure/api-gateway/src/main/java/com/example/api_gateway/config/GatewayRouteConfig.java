package com.example.api_gateway.config;

import com.example.api_gateway.filter.CorrelationIdFilter;
import com.example.api_gateway.filter.GatewayRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.*;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRouteConfig {
    @Bean
    public RouterFunction<ServerResponse> productRoute(){
        return route("product-service")
                .route(path("/api/product/**"),http())
                .before(uri("http://localhost:8081"))
                .before(stripPrefix(1))
                .before(CorrelationIdFilter.addCorrelationId())
                .filter(GatewayRequestLoggingFilter.logRequests())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoute(){
        return route("order-service")
                .route(path("/api/order/**"),http())
                .before(uri("http://localhost:8082"))
                .before(stripPrefix(1))
                .before(CorrelationIdFilter.addCorrelationId())
                .filter(GatewayRequestLoggingFilter.logRequests())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationRoute(){
        return route("notification-service")
                .route(path("/api/notification/**"),http())
                .before(uri("http://localhost:8083"))
                .before(stripPrefix(1))
                .before(CorrelationIdFilter.addCorrelationId())
                .filter(GatewayRequestLoggingFilter.logRequests())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryRoute(){
        return route("inventory-service")
                .route(path("/api/inventory/**"),http())
                .before(uri("http://localhost:8084"))
                .before(stripPrefix(1))
                .before(CorrelationIdFilter.addCorrelationId())
                .filter(GatewayRequestLoggingFilter.logRequests())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> trackingRoute(){
        return route("tracking-route")
                .route(path("/api/tracking/**"),http())
                .before(uri("http://localhost:8085"))
                .before(stripPrefix(1))
                .before(CorrelationIdFilter.addCorrelationId())
                .filter(GatewayRequestLoggingFilter.logRequests())
                .build();
    }
}

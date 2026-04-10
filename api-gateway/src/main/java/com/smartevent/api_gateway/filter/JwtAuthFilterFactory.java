package com.smartevent.api_gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final JwtAuthFilter jwtAuthFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return jwtAuthFilter;
    }
}
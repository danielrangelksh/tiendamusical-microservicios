package com.tiendamusical.api_gateway.filter;

import com.tiendamusical.api_gateway.security.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticatorFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    private final List<String> rutasPublicas = List.of(
            "/auth/login",
            "/auth/publico",
            "/api/clientes/publico",
            "/api/productos/publico",
            "/api/pedidos/publico",
            "/api/reclamos/publico",
            "/api/devoluciones/publico",
            "/api/servicioTecnico/publico"
    );

    public JwtAuthenticatorFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        boolean esPublica = rutasPublicas.stream().anyMatch(path::equals);
        if (esPublica) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.esTokenValido(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
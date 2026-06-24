package com.duoc.pedidos.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ProductoClient {
    private final RestClient restClient;

    public ProductoClient(@Value("${producto-service.url}") String productoServidor){
        this.restClient = RestClient.builder().baseUrl(productoServidor).build();
    }

    public Map<String, Object> obtenerProductoId(Integer id, String token){
        return this.restClient.get()
                .uri("/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    throw new RuntimeException("Producto no encontrado");
                })
                .body(Map.class);
    }
}
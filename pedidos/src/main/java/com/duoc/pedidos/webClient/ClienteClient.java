package com.duoc.pedidos.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ClienteClient {
    private final RestClient restClient;

    public ClienteClient(@Value("${cliente-service.url}") String clienteServidor){
        this.restClient = RestClient.builder().baseUrl(clienteServidor).build();
    }

    public Map<String, Object> obtenerClienteId(Integer id, String token){
        return this.restClient.get()
                .uri("/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    throw new RuntimeException("Cliente no encontrado");
                })
                .body(Map.class);
    }
}
package com.fullstack.servicioTecnico.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class PedidosClient {
    private final RestClient restClient;

    public PedidosClient(@Value("${pedidos-service.url}") String pedidoServidor){
        this.restClient = RestClient.builder().baseUrl(pedidoServidor).build();
    }

    public Map<String, Object> obtenerPedidoId(Integer id, String token){
        return this.restClient.get()
                .uri("/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    throw new RuntimeException("Pedido no encontrado");
                })
                .body(Map.class);
    }
}

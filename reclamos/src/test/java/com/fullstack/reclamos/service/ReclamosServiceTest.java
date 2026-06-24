package com.fullstack.reclamos.service;

import com.fullstack.reclamos.dto.ReclamosRequest;
import com.fullstack.reclamos.model.Reclamos;
import com.fullstack.reclamos.repository.ReclamosRepository;
import com.fullstack.reclamos.webClient.ClienteClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReclamosServiceTest {

    @Mock
    private ReclamosRepository reclamosRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private ReclamosService reclamosService;

    @Test
    @DisplayName("Crear reclamo con cliente válido arma el nombre completo y lo guarda")
    void crearDesdeRequest_conClienteValido_guardaReclamoConNombreCompleto() {
        ReclamosRequest request = new ReclamosRequest(null, "Producto defectuoso", "El amplificador no enciende", 1);
        Map<String, Object> cliente = Map.of("nombre", "Juan", "apellido", "Pérez");
        given(clienteClient.obtenerClienteId(1, "Bearer token")).willReturn(cliente);
        given(reclamosRepository.save(any(Reclamos.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Reclamos resultado = reclamosService.crearDesdeRequest(request, "Bearer token");

        assertThat(resultado.getNombreCliente()).isEqualTo("Juan Pérez");
        assertThat(resultado.getAsunto()).isEqualTo("Producto defectuoso");
        assertThat(resultado.getClienteId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Crear reclamo con cliente inexistente lanza excepción y no guarda nada")
    void crearDesdeRequest_conClienteInexistente_lanzaExcepcion() {
        ReclamosRequest request = new ReclamosRequest(null, "Producto defectuoso", "El amplificador no enciende", 99);
        given(clienteClient.obtenerClienteId(99, "Bearer token")).willReturn(Map.of());

        assertThatThrownBy(() -> reclamosService.crearDesdeRequest(request, "Bearer token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente no encontrado, no se puede agregar reclamo");

        verify(reclamosRepository, never()).save(any());
    }
}

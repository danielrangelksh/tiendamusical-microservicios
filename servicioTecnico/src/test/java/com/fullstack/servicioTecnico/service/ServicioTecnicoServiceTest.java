package com.fullstack.servicioTecnico.service;

import com.fullstack.servicioTecnico.dto.ServicioTecnicoRequest;
import com.fullstack.servicioTecnico.model.ServicioTecnico;
import com.fullstack.servicioTecnico.repository.ServicioTecnicoRepository;
import com.fullstack.servicioTecnico.webClient.PedidosClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServicioTecnicoServiceTest {

    @Mock
    private ServicioTecnicoRepository servicioTecnicoRepository;

    @Mock
    private PedidosClient pedidosClient;

    @InjectMocks
    private ServicioTecnicoService servicioTecnicoService;

    @Test
    @DisplayName("Crear servicio técnico con pedido válido arma el registro consolidado")
    void crearDesdeRequest_conPedidoValido_armaRegistroConsolidado() {
        ServicioTecnicoRequest request = new ServicioTecnicoRequest();
        request.setPedidoId(1);
        request.setFalla("No enciende");
        request.setDescripcion("El amplificador no enciende al conectarlo");
        request.setEstado("RECIBIDO");

        Map<String, Object> pedido = Map.of(
                "nombreCliente", "Juan Pérez",
                "numeroSerie", "SN-001",
                "nombreProducto", "Marshall JVM410H"
        );
        given(pedidosClient.obtenerPedidoId(1, "Bearer token")).willReturn(pedido);
        given(servicioTecnicoRepository.save(any(ServicioTecnico.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ServicioTecnico resultado = servicioTecnicoService.crearDesdeRequest(request, "Bearer token");

        assertThat(resultado.getNombreCliente()).isEqualTo("Juan Pérez");
        assertThat(resultado.getNombreProducto()).isEqualTo("Marshall JVM410H");
        assertThat(resultado.getFalla()).isEqualTo("No enciende");
    }

    @Test
    @DisplayName("Crear servicio técnico con pedido inexistente lanza excepción y no guarda nada")
    void crearDesdeRequest_conPedidoInexistente_lanzaExcepcion() {
        ServicioTecnicoRequest request = new ServicioTecnicoRequest();
        request.setPedidoId(99);
        request.setFalla("No enciende");
        request.setDescripcion("El amplificador no enciende");
        request.setEstado("RECIBIDO");
        given(pedidosClient.obtenerPedidoId(99, "Bearer token")).willReturn(Map.of());

        assertThatThrownBy(() -> servicioTecnicoService.crearDesdeRequest(request, "Bearer token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error: El pedido no existe en el catálogo principal.");

        verify(servicioTecnicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar un servicio técnico existente lo borra y devuelve true")
    void eliminar_conRegistroExistente_devuelveTrue() {
        ServicioTecnico existente = new ServicioTecnico();
        existente.setId(1);
        given(servicioTecnicoRepository.findById(1)).willReturn(Optional.of(existente));

        boolean resultado = servicioTecnicoService.eliminar(1);

        assertThat(resultado).isTrue();
        verify(servicioTecnicoRepository).delete(existente);
    }
}

package com.duoc.pedidos.service;

import com.duoc.pedidos.dto.PedidoRequest;
import com.duoc.pedidos.model.Pedido;
import com.duoc.pedidos.repository.PedidoRepository;
import com.duoc.pedidos.webClient.ClienteClient;
import com.duoc.pedidos.webClient.ProductoClient;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    @DisplayName("Crear pedido con cliente y producto válidos arma el pedido consolidado")
    void crearDesdeRequest_conClienteYProductoValidos_armaPedidoConsolidado() {
        PedidoRequest request = new PedidoRequest(1, 1, "Av. Siempre Viva 742", 5000, null);
        Map<String, Object> cliente = Map.of("nombre", "Juan", "apellido", "Pérez");
        Map<String, Object> producto = Map.of(
                "nombreProducto", "Fender Stratocaster",
                "descripcion", "Guitarra eléctrica",
                "tipoInstrumento", "Cuerdas",
                "ventaArriendo", "VENTA",
                "estado", "DISPONIBLE",
                "precioInstrumento", 1200000
        );
        given(clienteClient.obtenerClienteId(1, "Bearer token")).willReturn(cliente);
        given(productoClient.obtenerProductoId(1, "Bearer token")).willReturn(producto);
        given(pedidoRepository.save(any(Pedido.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.crearDesdeRequest(request, "Bearer token");

        assertThat(resultado.getNombreCliente()).isEqualTo("Juan Pérez");
        assertThat(resultado.getNombreProducto()).isEqualTo("Fender Stratocaster");
        assertThat(resultado.getEstado()).isEqualTo("DISPONIBLE");
        assertThat(resultado.getPrecioInstrumento()).isEqualTo(1200000);
        assertThat(resultado.getDireccion()).isEqualTo("Av. Siempre Viva 742");
    }

    @Test
    @DisplayName("Crear pedido con cliente inexistente lanza excepción y no consulta productos")
    void crearDesdeRequest_conClienteInexistente_lanzaExcepcionYNoConsultaProductos() {
        // Given
        PedidoRequest request = new PedidoRequest(1, 99, "Av. Siempre Viva 742", 5000, null);
        given(clienteClient.obtenerClienteId(99, "Bearer token")).willReturn(Map.of());

        assertThatThrownBy(() -> pedidoService.crearDesdeRequest(request, "Bearer token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente no encontrado");

        verify(productoClient, never()).obtenerProductoId(any(), any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear pedido con producto inexistente lanza excepción")
    void crearDesdeRequest_conProductoInexistente_lanzaExcepcion() {
        PedidoRequest request = new PedidoRequest(99, 1, "Av. Siempre Viva 742", 5000, null);
        Map<String, Object> cliente = Map.of("nombre", "Juan", "apellido", "Pérez");
        given(clienteClient.obtenerClienteId(1, "Bearer token")).willReturn(cliente);
        given(productoClient.obtenerProductoId(99, "Bearer token")).willReturn(Map.of());

        assertThatThrownBy(() -> pedidoService.crearDesdeRequest(request, "Bearer token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Producto no encontrado");

        verify(pedidoRepository, never()).save(any());
    }
}

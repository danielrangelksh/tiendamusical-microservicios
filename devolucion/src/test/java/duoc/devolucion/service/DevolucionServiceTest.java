package duoc.devolucion.service;

import duoc.devolucion.dto.DevolucionRequest;
import duoc.devolucion.model.Devolucion;
import duoc.devolucion.repository.DevolucionRepository;
import duoc.devolucion.webclient.PedidosClient;
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
class DevolucionServiceTest {

    @Mock
    private DevolucionRepository devolucionRepository;

    @Mock
    private PedidosClient pedidosClient;

    @InjectMocks
    private DevolucionService devolucionService;

    @Test
    @DisplayName("Crear devolución con pedido válido arma el registro consolidado")
    void crearDesdeRequest_conPedidoValido_armaDevolucionConsolidada() {
        DevolucionRequest request = new DevolucionRequest();
        request.setPedidoId(1);
        request.setRequerimiento("REEMBOLSO");
        request.setMotivo("El producto llegó dañado");

        Map<String, Object> pedido = Map.of(
                "numeroSerie", "SN-001",
                "nombreProducto", "Fender Stratocaster",
                "precioInstrumento", 1200000,
                "nombreCliente", "Juan Pérez"
        );
        given(pedidosClient.obtenerPedidoId(1, "Bearer token")).willReturn(pedido);
        given(devolucionRepository.save(any(Devolucion.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Devolucion resultado = devolucionService.crearDesdeRequest(request, "Bearer token");

        assertThat(resultado.getNombreCliente()).isEqualTo("Juan Pérez");
        assertThat(resultado.getNombreProducto()).isEqualTo("Fender Stratocaster");
        assertThat(resultado.getRequerimiento()).isEqualTo("REEMBOLSO");
    }

    @Test
    @DisplayName("Crear devolución con pedido inexistente lanza excepción y no guarda nada")
    void crearDesdeRequest_conPedidoInexistente_lanzaExcepcion() {
        DevolucionRequest request = new DevolucionRequest();
        request.setPedidoId(99);
        request.setRequerimiento("CAMBIO");
        request.setMotivo("Talla incorrecta");
        given(pedidosClient.obtenerPedidoId(99, "Bearer token")).willReturn(Map.of());

        assertThatThrownBy(() -> devolucionService.crearDesdeRequest(request, "Bearer token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido no encontrado");

        verify(devolucionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar una devolución existente la borra y devuelve true")
    void eliminar_conDevolucionExistente_devuelveTrue() {
        Devolucion existente = new Devolucion();
        given(devolucionRepository.findById(1)).willReturn(Optional.of(existente));

        boolean resultado = devolucionService.eliminar(1);

        assertThat(resultado).isTrue();
        verify(devolucionRepository).deleteById(1);
    }
}
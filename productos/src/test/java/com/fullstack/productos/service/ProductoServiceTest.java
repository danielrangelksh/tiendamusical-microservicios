package com.fullstack.productos.service;

import com.fullstack.productos.dto.ProductosRequest;
import com.fullstack.productos.model.Producto;
import com.fullstack.productos.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("Crear producto desde request guarda los datos correctamente")
    void crearDesdeRequest_guardaProductoConDatosDelRequest() {
        ProductosRequest request = new ProductosRequest(
                "SN-001", "Fender Stratocaster", "Cuerdas", "Guitarra eléctrica",
                1200000, 45000, "VENTA", "DISPONIBLE", null);
        given(productoRepository.save(any(Producto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Producto resultado = productoService.crearDesdeRequest(request);

        assertThat(resultado.getNombreProducto()).isEqualTo("Fender Stratocaster");
        assertThat(resultado.getEstado()).isEqualTo("DISPONIBLE");
        assertThat(resultado.getPrecioInstrumento()).isEqualTo(1200000);
    }

    @Test
    @DisplayName("Actualizar un producto existente modifica y guarda sus datos")
    void actualizar_conProductoExistente_actualizaYGuarda() {
        Producto existente = new Producto(1, "SN-001", "Fender Stratocaster", "Cuerdas",
                "Guitarra eléctrica", 1200000, 45000, "VENTA", "DISPONIBLE", null);
        ProductosRequest request = new ProductosRequest(
                "SN-001", "Fender Stratocaster", "Cuerdas", "Guitarra eléctrica",
                1100000, 45000, "VENTA", "VENDIDO", null);
        given(productoRepository.findById(1)).willReturn(Optional.of(existente));
        given(productoRepository.save(any(Producto.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Producto actualizado = productoService.actualizar(1, request);

        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getEstado()).isEqualTo("VENDIDO");
        assertThat(actualizado.getPrecioInstrumento()).isEqualTo(1100000);
    }

    @Test
    @DisplayName("Actualizar un producto que no existe devuelve null y no guarda nada")
    void actualizar_conProductoInexistente_devuelveNullYNoGuarda() {
        given(productoRepository.findById(99)).willReturn(Optional.empty());

        Producto resultado = productoService.actualizar(99, new ProductosRequest());

        assertThat(resultado).isNull();
        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar un producto existente lo borra y devuelve true")
    void eliminar_conProductoExistente_devuelveTrue() {
        Producto existente = new Producto(1, "SN-001", "Fender Stratocaster", "Cuerdas",
                "Guitarra eléctrica", 1200000, 45000, "VENTA", "DISPONIBLE", null);
        given(productoRepository.findById(1)).willReturn(Optional.of(existente));

        boolean resultado = productoService.eliminar(1);

        assertThat(resultado).isTrue();
        verify(productoRepository).deleteById(1);
    }

    @Test
    @DisplayName("Eliminar un producto que no existe devuelve false")
    void eliminar_conProductoInexistente_devuelveFalse() {
        given(productoRepository.findById(99)).willReturn(Optional.empty());

        boolean resultado = productoService.eliminar(99);

        assertThat(resultado).isFalse();
    }
}

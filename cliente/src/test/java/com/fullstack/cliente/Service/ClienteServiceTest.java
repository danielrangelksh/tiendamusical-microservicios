package com.fullstack.cliente.Service;

import com.fullstack.cliente.dto.ClienteRequest;
import com.fullstack.cliente.model.Cliente;
import com.fullstack.cliente.repository.ClienteRepository;
import com.fullstack.cliente.service.ClienteService;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Crear cliente desde request guarda los datos correctamente")
    void crearDesdeRequest_guardaClienteConDatosDelRequest() {

        ClienteRequest request = new ClienteRequest("11.111.111-1", "Juan", "Pérez", null, "juan@correo.com");
        given(clienteRepository.save(any(Cliente.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = clienteService.crearDesdeRequest(request);

        assertThat(resultado.getRun()).isEqualTo("11.111.111-1");
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        assertThat(resultado.getCorreo()).isEqualTo("juan@correo.com");
    }

    @Test
    @DisplayName("Actualizar un cliente existente modifica y guarda sus datos")
    void actualizar_conClienteExistente_actualizaYGuarda() {
        Cliente existente = new Cliente(1, "11.111.111-1", "Juan", "Pérez", null, "juan@correo.com");
        ClienteRequest request = new ClienteRequest("11.111.111-1", "Juan Carlos", "Pérez", null, "nuevo@correo.com");
        given(clienteRepository.findById(1)).willReturn(Optional.of(existente));
        given(clienteRepository.save(any(Cliente.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Cliente actualizado = clienteService.actualizar(1, request);

        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getNombre()).isEqualTo("Juan Carlos");
        assertThat(actualizado.getCorreo()).isEqualTo("nuevo@correo.com");
    }

    @Test
    @DisplayName("Actualizar un cliente que no existe devuelve null y no guarda nada")
    void actualizar_conClienteInexistente_devuelveNullYNoGuarda() {
        given(clienteRepository.findById(99)).willReturn(Optional.empty());

        Cliente resultado = clienteService.actualizar(99, new ClienteRequest());

        assertThat(resultado).isNull();
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar un cliente existente lo borra y devuelve true")
    void eliminar_conClienteExistente_devuelveTrue() {
        Cliente existente = new Cliente(1, "11.111.111-1", "Juan", "Pérez", null, "juan@correo.com");
        given(clienteRepository.findById(1)).willReturn(Optional.of(existente));

        boolean resultado = clienteService.eliminar(1);

        assertThat(resultado).isTrue();
        verify(clienteRepository).deleteById(1);
    }

    @Test
    @DisplayName("Eliminar un cliente que no existe devuelve false")
    void eliminar_conClienteInexistente_devuelveFalse() {
        given(clienteRepository.findById(99)).willReturn(Optional.empty());

        boolean resultado = clienteService.eliminar(99);

        assertThat(resultado).isFalse();
        verify(clienteRepository, never()).deleteById(any());
    }
}
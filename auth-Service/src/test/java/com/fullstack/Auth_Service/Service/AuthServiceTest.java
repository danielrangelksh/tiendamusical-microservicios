package com.fullstack.Auth_Service.Service;

import com.fullstack.Auth_Service.dto.LoginResponse;
import com.fullstack.Auth_Service.exception.MalCredencialException;
import com.fullstack.Auth_Service.model.Usuario;
import com.fullstack.Auth_Service.repository.UsuarioRepository;
import com.fullstack.Auth_Service.service.AuthService;
import com.fullstack.Auth_Service.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Login con credenciales válidas devuelve un token y los datos del usuario")
    void login_conCredencialesValidas_devuelveTokenYDatosDelUsuario() {

        Usuario usuario = new Usuario(1, "admin", "1234", "ADMIN");
        given(usuarioRepository.findByUsername("admin")).willReturn(Optional.of(usuario));
        given(jwtService.generarToken("admin", "ADMIN")).willReturn("token-falso-123");

        LoginResponse response = authService.login("admin", "1234");

        assertThat(response.getToken()).isEqualTo("token-falso-123");
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRol()).isEqualTo("ADMIN");
        assertThat(response.getMensaje()).isEqualTo("Usuario autorizado");
    }

    @Test
    @DisplayName("Login con un usuario que no existe lanza MalCredencialException")
    void login_conUsuarioInexistente_lanzaMalCredencialException() {

        given(usuarioRepository.findByUsername("fantasma")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("fantasma", "1234"))
                .isInstanceOf(MalCredencialException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("Login con password incorrecta lanza MalCredencialException")
    void login_conPasswordIncorrecta_lanzaMalCredencialException() {

        Usuario usuario = new Usuario(1, "admin", "1234", "ADMIN");
        given(usuarioRepository.findByUsername("admin")).willReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login("admin", "clave-equivocada"))
                .isInstanceOf(MalCredencialException.class)
                .hasMessage("Password incorrecta");
    }
}
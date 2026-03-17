package com.EcoSoftware.Scrum6.Controller;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Security.TokenJWT;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenJWT tokenJWT;

    @InjectMocks
    private AuthController authController;

    private UsuarioEntity usuarioMock;
    private RolEntity rolMock;

    @BeforeEach
    void setUp() {
        // Configurar mocks
        rolMock = new RolEntity();
        rolMock.setNombre("Ciudadano");

        usuarioMock = new UsuarioEntity();
        usuarioMock.setIdUsuario(1L);
        usuarioMock.setCorreo("test@test.com");
        usuarioMock.setContrasena("encodedPassword");
        usuarioMock.setEstado(true);
        usuarioMock.setRol(rolMock);
    }

    // ✅ TEST 1: Login exitoso
    @Test
    void login_CuandoCredencialesSonCorrectas_DebeRetornarToken() {
        // Arrange (Preparar)
        Map<String, String> request = Map.of(
                "correo", "test@test.com",
                "contrasena", "password123+"
        );

        when(usuarioRepository.findByCorreoAndEstadoTrue("test@test.com"))
                .thenReturn(Optional.of(usuarioMock));
        
                when(passwordEncoder.matches("password123+", "encodedPassword"))
        .thenReturn(true);

        when(tokenJWT.generarToken("test@test.com"))
                .thenReturn("jwt-token-mock");

        // Act (Ejecutar)
        var response = authController.login(request);

        // Assert (Verificar)
        assertEquals(200, response.getStatusCodeValue());

        var body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Inicio de sesión exitoso", body.get("mensaje"));
        assertEquals("jwt-token-mock", body.get("token"));
        assertEquals("test@test.com", body.get("correo"));
        assertEquals("Ciudadano", body.get("rol"));

        // Verificar que los mocks fueron llamados
        verify(usuarioRepository, times(1)).findByCorreoAndEstadoTrue("test@test.com");
        verify(passwordEncoder, times(1)).matches("password123+", "encodedPassword");
        verify(tokenJWT, times(1)).generarToken("test@test.com");
    }

    // ✅ TEST 2: Usuario no existe
    @Test
    void login_CuandoUsuarioNoExiste_DebeRetornarNoAutorizado() {
        // Arrange
        Map<String, String> request = Map.of(
                "correo", "noexiste@test.com",
                "contrasena", "password123+"
        );

        when(usuarioRepository.findByCorreoAndEstadoTrue("noexiste@test.com"))
                .thenReturn(Optional.empty());

        // Act
        var response = authController.login(request);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        var body = (Map<?, ?>) response.getBody();
        assertEquals("Correo no registrado o usuario inactivo", body.get("error"));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ✅ TEST 3: Contraseña incorrecta
    @Test
    void login_CuandoContraseñaIncorrecta_DebeRetornarNoAutorizado() {
        // Arrange
        Map<String, String> request = Map.of(
                "correo", "test@test.com",
                "contrasena", "password-incorrecta"
        );

        when(usuarioRepository.findByCorreoAndEstadoTrue("test@test.com"))
                .thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("password-incorrecta", "encodedPassword"))
                .thenReturn(false);

        // Act
        var response = authController.login(request);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        var body = (Map<?, ?>) response.getBody();
        assertEquals("Contraseña incorrecta", body.get("error"));
    }

    // ✅ TEST 4: Usuario inactivo (estado false)
    @Test
    void login_CuandoUsuarioInactivo_DebeRetornarNoAutorizado() {
        // Arrange
        usuarioMock.setEstado(false);
        Map<String, String> request = Map.of(
                "correo", "test@test.com",
                "contrasena", "password123"
        );

        when(usuarioRepository.findByCorreoAndEstadoTrue("test@test.com"))
                .thenReturn(Optional.empty()); // Porque busca solo con estado true

        // Act
        var response = authController.login(request);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
    }
}
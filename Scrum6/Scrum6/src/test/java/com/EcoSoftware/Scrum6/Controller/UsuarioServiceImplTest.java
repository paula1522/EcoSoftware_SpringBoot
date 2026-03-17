// Archivo: src/test/java/com/EcoSoftware/Scrum6/Implement/UsuarioServiceImplTest.java
package com.EcoSoftware.Scrum6.Controller;  // ¡CORREGIDO!

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;
import com.EcoSoftware.Scrum6.Implement.UsuarioServiceImpl;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.CloudinaryService;
import com.EcoSoftware.Scrum6.Service.EmailService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioEntity usuarioEntity;
    private UsuarioDTO usuarioDTO;
    private RolEntity rolEntity;

    @BeforeEach
    void setUp() {

        rolEntity = new RolEntity();
        rolEntity.setIdRol(1L);
        rolEntity.setNombre("Ciudadano");

        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setIdUsuario(1L);
        usuarioEntity.setNombre("María López");
        usuarioEntity.setCorreo("maria@test.com");
        usuarioEntity.setCedula("987654321");
        usuarioEntity.setTelefono("3012345678");
        usuarioEntity.setContrasena("passwordEncriptada");
        usuarioEntity.setEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR);
        usuarioEntity.setRol(rolEntity);
        usuarioEntity.setFechaCreacion(LocalDateTime.now());

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1L);
        usuarioDTO.setNombre("María López");
        usuarioDTO.setCorreo("maria@test.com");
        usuarioDTO.setRolId(1L);
        usuarioDTO.setCedula("987654321");
        usuarioDTO.setTelefono("3012345678");
        usuarioDTO.setContrasena("Password123+");
    }

    // LISTAR USUARIOS
    @Test
    void listarUsuarios_DebeRetornarLista() {

        when(usuarioRepository.findAllByOrderByIdUsuarioAsc())
                .thenReturn(List.of(usuarioEntity));

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(usuarioRepository).findAllByOrderByIdUsuarioAsc();
    }

    // OBTENER POR ID
    @Test
    void obtenerUsuarioPorId_CuandoExiste() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        UsuarioDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(resultado);
        assertEquals("María López", resultado.getNombre());
    }

    // OBTENER POR ID ERROR
    @Test
    void obtenerUsuarioPorId_CuandoNoExiste() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioService.obtenerUsuarioPorId(99L));

        assertTrue(ex.getMessage().contains("Persona no encontrada"));
    }

    // CREAR USUARIO
    @Test
    void crearUsuario_DebeGuardarYEnviarCorreo() {

        when(modelMapper.map(usuarioDTO, UsuarioEntity.class))
                .thenReturn(usuarioEntity);

        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(rolEntity));

        when(passwordEncoder.encode("Password123+"))
                .thenReturn("passwordEncriptada");

        when(usuarioRepository.save(any()))
                .thenReturn(usuarioEntity);

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        when(templateEngine.process(eq("email-bienvenida"), any()))
                .thenReturn("<h1>Bienvenida</h1>");

        UsuarioDTO resultado = usuarioService.crearUsuario(usuarioDTO);

        assertNotNull(resultado);

        verify(usuarioRepository).save(any());
        verify(emailService).enviarCorreo(
                eq("maria@test.com"),
                eq("¡Bienvenido a EcoSoftware!"),
                anyString()
        );
    }

    // CREAR USUARIO SIN ROL
    @Test
    void crearUsuario_SinRol_DebeLanzarError() {

        usuarioDTO.setRolId(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioService.crearUsuario(usuarioDTO));

        assertEquals("El rol es obligatorio", ex.getMessage());
    }

    // APROBAR USUARIO
    @Test
    void aprobarUsuario_DebeCambiarEstado() {

        usuarioEntity.setEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));

        usuarioService.aprobarUsuario(1L);

        assertEquals(EstadoRegistro.APROBADO, usuarioEntity.getEstadoRegistro());

        verify(usuarioRepository).save(usuarioEntity);
    }

    // RECHAZAR USUARIO
    @Test
    void rechazarUsuario_DebeCambiarEstado() {

        usuarioEntity.setEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));

        usuarioService.rechazarUsuario(1L);

        assertEquals(EstadoRegistro.RECHAZADO, usuarioEntity.getEstadoRegistro());

        verify(usuarioRepository).save(usuarioEntity);
    }

    // CONTAR PENDIENTES
    @Test
    void contarUsuariosPendientes() {

        when(usuarioRepository.countByEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR))
                .thenReturn(3L);

        Long resultado = usuarioService.contarUsuariosPendientes();

        assertEquals(3L, resultado);
    }

    // GENERAR PLANTILLA
    @Test
    void generarPlantillaExcelPorRol() {

        byte[] plantilla = usuarioService.generarPlantillaExcelPorRol("Reciclador");

        assertNotNull(plantilla);
        assertTrue(plantilla.length > 0);
    }

    // ACTUALIZAR USUARIO
   @Test
void actualizarUsuario() {

    UsuarioEditarDTO editarDTO = new UsuarioEditarDTO();
    editarDTO.setNombre("Nuevo Nombre");

    when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuarioEntity));

    // ESTE ES EL MAP QUE USA TU SERVICIO
    doNothing().when(modelMapper)
            .map(any(UsuarioEditarDTO.class), any(UsuarioEntity.class));

    when(usuarioRepository.save(any()))
            .thenReturn(usuarioEntity);

    when(modelMapper.map(usuarioEntity, UsuarioEditarDTO.class))
            .thenReturn(editarDTO);

    UsuarioEditarDTO resultado = usuarioService.actualizarUsuario(1L, editarDTO);

    assertNotNull(resultado);
    assertEquals("Nuevo Nombre", resultado.getNombre());

    verify(usuarioRepository).save(any());
}


    // ELIMINACION LOGICA
    @Test
    void eliminacionLogica() {

        when(usuarioRepository.eliminacionLogica(1L))
                .thenReturn(1);

        usuarioService.eliminacionPorEstado(1L);

        verify(usuarioRepository).eliminacionLogica(1L);
    }

}

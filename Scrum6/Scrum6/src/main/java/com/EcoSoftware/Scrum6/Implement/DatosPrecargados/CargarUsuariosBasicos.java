package com.EcoSoftware.Scrum6.Implement.DatosPrecargados;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Util.PasswordPolicyUtil;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CargarUsuariosBasicos implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public CargarUsuariosBasicos(UsuarioRepository usuarioRepository,
                                 RolRepository rolRepository,
                                 PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

                crearOActualizarUsuarioBase(
                "jaiandroaber@gmail.com",   // correo
                "Administrador",            // nombre
                "Administrador#2026",            // contraseña
                "1000070000",               // cédula
                "3070000000",               // teléfono
                RolEntity.TipoDeRol.Administrador,
                "Teusaquillo",              // localidad
                EstadoRegistro.APROBADO      // estadoRegistro
        );

        crearOActualizarUsuarioBase(
                "paula06sepulveda@gmail.com",
                "Ciudadano",
                "Ciudadano#2026",
                "2000090070",
                "3000041111",
                RolEntity.TipoDeRol.Ciudadano,
                "Ciudad_Bolivar",              
                EstadoRegistro.APROBADO      // estadoRegistro
        );

        crearOActualizarUsuarioBase(
                "danacastro2014@gmail.com",
                "Empresa",
                "Empresa#2026",
                "30071200000",
                "3450002222",
                RolEntity.TipoDeRol.Empresa,
                "Antonio_Nariño",              
                EstadoRegistro.PENDIENTE_REVISAR     // estadoRegistro
        );

        crearOActualizarUsuarioBase(
                "ecosoftware2025@gmail.com",
                "Reciclador",
                "Recicla#2026",
                "400047250000",
                "3700003333",
                RolEntity.TipoDeRol.Reciclador,
                "Bosa",              
                EstadoRegistro.PENDIENTE_REVISAR     // estadoRegistro
        );

        System.out.println(">>> Usuarios base verificados/creados correctamente");
    }

        private void crearOActualizarUsuarioBase(
            String correo,
            String nombre,
            String contrasenaPlano,
            String cedula,
            String telefono,
            RolEntity.TipoDeRol tipoRol,
            String localidad,
            EstadoRegistro estadoRegistro
    ) {

        PasswordPolicyUtil.validar(contrasenaPlano);

        UsuarioEntity usuarioExistente = usuarioRepository.findByCorreoIgnoreCase(correo)
                .or(() -> usuarioRepository.findByCedulaAndEstadoTrue(cedula))
                .orElse(null);

        if (usuarioExistente != null) {
            usuarioExistente.setContrasena(passwordEncoder.encode(contrasenaPlano));
            usuarioRepository.save(usuarioExistente);
            System.out.println(">>> Contraseña actualizada para usuario base: " + correo);
            return;
        }

        // Buscar rol
        RolEntity rol = rolRepository.findByTipo(tipoRol)
                .orElseThrow(() -> new RuntimeException("ERROR: El rol " + tipoRol + " no existe aún."));

        UsuarioEntity usuario = new UsuarioEntity();
        // Datos ingresados
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(contrasenaPlano));
        usuario.setCedula(cedula);
        usuario.setTelefono(telefono);
        usuario.setRol(rol);
        usuario.setEstadoRegistro(estadoRegistro);

        usuario.setEstado(true);
        usuario.setDireccion(null);
        usuario.setBarrio(null);
        usuario.setLocalidad(localidad);
        usuario.setNit(null);
        usuario.setRepresentanteLegal(null);
        usuario.setZona_de_trabajo(null);
        usuario.setHorario(null);
        usuario.setTipoMaterial(null);
        usuario.setCantidad_minima(null);
        usuario.setImagen_perfil(null);
        usuario.setCertificaciones(null);

        usuarioRepository.save(usuario);

        System.out.println(">>> Usuario creado: " + correo);
    }
}

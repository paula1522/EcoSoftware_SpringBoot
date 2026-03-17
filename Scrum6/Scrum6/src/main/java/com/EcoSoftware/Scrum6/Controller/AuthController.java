package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Security.TokenJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenJWT tokenJWT;

   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

    String correo = request.get("correo");
    String contrasena = request.get("contrasena");

    Optional<UsuarioEntity> usuarioOpt =
            usuarioRepository.findByCorreoAndEstadoTrue(correo);

    if (usuarioOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Correo no registrado o usuario inactivo"));
    }

    UsuarioEntity usuario = usuarioOpt.get();

    if (usuario.getEstadoRegistro() == EstadoRegistro.RECHAZADO) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Tu registro fue rechazado por el administrador"));
    }

    if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Contraseña incorrecta"));
    }

    String token = tokenJWT.generarToken(usuario.getCorreo());

    Map<String, Object> response = new HashMap<>();
    response.put("mensaje", "Inicio de sesión exitoso");
    response.put("token", token);
    response.put("correo", usuario.getCorreo());
    response.put("rol", usuario.getRol().getNombre());
    response.put("idUsuario", usuario.getIdUsuario());
    response.put("estadoRegistro", usuario.getEstadoRegistro());

    return ResponseEntity.ok(response);
}}
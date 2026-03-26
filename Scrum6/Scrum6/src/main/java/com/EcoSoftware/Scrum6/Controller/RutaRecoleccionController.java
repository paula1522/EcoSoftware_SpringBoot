package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaRecoleccionController {

    private final RutaRecoleccionService rutaService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<RutaRecoleccionDTO> crearRuta(@RequestBody RutaRecoleccionDTO dto) {
        UsuarioEntity recolector = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(rutaService.crearRuta(dto, recolector.getIdUsuario()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rutaService.obtenerPorId(id));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RutaRecoleccionDTO>> listarTodas() {
        return ResponseEntity.ok(rutaService.listarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarPorEstado(@PathVariable EstadoRuta estado) {
        return ResponseEntity.ok(rutaService.listarPorEstado(estado));
    }

    @GetMapping("/mis-rutas")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarMisRutas() {
        UsuarioEntity recolector = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(rutaService.listarPorRecolector(recolector.getIdUsuario()));
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<RutaRecoleccionDTO> iniciarRuta(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.iniciarRuta(id));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<RutaRecoleccionDTO> finalizarRuta(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.finalizarRuta(id));
    }

    private UsuarioEntity obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        return usuarioRepository.findByCorreoAndEstadoTrue(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));
    }
}
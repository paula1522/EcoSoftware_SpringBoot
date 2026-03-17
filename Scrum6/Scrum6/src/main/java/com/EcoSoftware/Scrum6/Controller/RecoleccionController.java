package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recolecciones")
@RequiredArgsConstructor
public class RecoleccionController {

    private final RecoleccionService recoleccionService;
    private final UsuarioRepository usuarioRepository;

    // ===========================
    // LISTAR TODAS LAS RECOLECCIONES
    // ===========================
    @GetMapping
    public ResponseEntity<List<RecoleccionDTO>> listarTodas() {
        return ResponseEntity.ok(recoleccionService.listarTodas());
    }

    // ===========================
    // OBTENER POR ID
    // ===========================
    @GetMapping("/{id}")
    public ResponseEntity<RecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        try {
            RecoleccionDTO dto = recoleccionService.obtenerPorId(id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===========================
    // LISTAR RECOLECCIONES DE UN RECOLECTOR
    // ===========================
    @GetMapping("/recolector/{id}")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRecolector(@PathVariable Long id) {
        return ResponseEntity.ok(recoleccionService.listarPorRecolector(id));
    }

    // ===========================
    // LISTAR RECOLECCIONES DEL RECOLECTOR AUTENTICADO
    // ===========================
    @GetMapping("/mis-recolecciones")
    public ResponseEntity<List<RecoleccionDTO>> listarMisRecolecciones() {
        UsuarioEntity recolector = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(recoleccionService.listarPorRecolector(recolector.getIdUsuario()));
    }

    // ===========================
    // LISTAR RECOLECCIONES EN PROCESO SIN RUTA
    // ===========================
    @GetMapping("/mis-recolecciones-en-proceso")
    public ResponseEntity<List<RecoleccionDTO>> listarMisRecoleccionesEnProcesoSinRuta() {
        UsuarioEntity recolector = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(recoleccionService.listarSinRutaPorRecolector(recolector.getIdUsuario()));
    }

    // ===========================
    // LISTAR RECOLECCIONES ACTIVAS
    // ===========================
    @GetMapping("/activas")
    public ResponseEntity<List<RecoleccionDTO>> listarActivas() {
        return ResponseEntity.ok(recoleccionService.listarActivas());
    }

    // ===========================
    // LISTAR RECOLECCIONES POR RUTA
    // ===========================
    @GetMapping("/ruta/{id}")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRuta(@PathVariable Long id) {
        return ResponseEntity.ok(recoleccionService.listarPorRuta(id));
    }

    // ===========================
    // ACTUALIZAR ESTADO
    // ===========================
    @PutMapping("/{id}/estado")
    public ResponseEntity<RecoleccionDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoRecoleccion estado
    ) {
        RecoleccionDTO dto = recoleccionService.actualizarEstado(id, estado);
        return ResponseEntity.ok(dto);
    }

    // ===========================
    // ACTUALIZAR DATOS DE RECOLECCIÓN
    // ===========================
    @PutMapping("/{id}")
    public ResponseEntity<RecoleccionDTO> actualizarRecoleccion(
            @PathVariable Long id,
            @RequestBody RecoleccionDTO dto
    ) {
        RecoleccionDTO actualizado = recoleccionService.actualizarRecoleccion(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // ===========================
    // ELIMINAR LÓGICAMENTE
    // ===========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogicamente(@PathVariable Long id) {
        recoleccionService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }

    // ===========================
    // MÉTODO AUXILIAR: OBTENER USUARIO AUTENTICADO
    // ===========================
    private UsuarioEntity obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        return usuarioRepository.findByCorreoAndEstadoTrue(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));
    }
}

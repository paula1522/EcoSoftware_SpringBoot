package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.CrearRutaRequestDTO;
import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
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

    // ===========================
    // CREAR RUTA
    // ===========================
    @PostMapping
    public ResponseEntity<RutaRecoleccionDTO> crearRuta(@RequestBody CrearRutaRequestDTO dto) {
        // Obtener usuario autenticado
        UsuarioEntity recolector = obtenerUsuarioAutenticado();

        // Asegurarse que el dto tenga el id del recolector
        dto.setRecolectorId(recolector.getIdUsuario());

        RutaRecoleccionDTO nuevaRuta = rutaService.crearRuta(dto);
        return ResponseEntity.ok(nuevaRuta);
    }

    // ===========================
    // OBTENER RUTA POR ID
    // ===========================
    @GetMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        RutaRecoleccionDTO ruta = rutaService.obtenerPorId(id);
        return ResponseEntity.ok(ruta);
    }

    // ===========================
    // LISTAR TODAS LAS RUTAS
    // ===========================
    @GetMapping
    public ResponseEntity<List<RutaRecoleccionDTO>> listarTodas() {
        return ResponseEntity.ok(rutaService.listarTodas());
    }

    // ===========================
    // LISTAR RUTAS DEL RECOLECTOR AUTENTICADO
    // ===========================
    @GetMapping("/mis-rutas")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarPorRecolector() {
        UsuarioEntity recolector = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(rutaService.listarPorRecolector(recolector.getIdUsuario()));
    }

    // ===========================
    // LISTAR RUTAS POR ESTADO
    // ===========================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarPorEstado(@PathVariable EstadoRuta estado) {
        return ResponseEntity.ok(rutaService.listarPorEstado(estado));
    }

    // ===========================
    // ACTUALIZAR ESTADO DE RUTA
    // ===========================
    @PutMapping("/{id}/estado")
    public ResponseEntity<RutaRecoleccionDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoRuta estado
    ) {
        RutaRecoleccionDTO rutaActualizada = rutaService.actualizarEstado(id, estado);
        return ResponseEntity.ok(rutaActualizada);
    }

    // ===========================
    // ASIGNAR RECOLECCIONES A RUTA
    // ===========================
    @PutMapping("/{id}/asignar")
    public ResponseEntity<RutaRecoleccionDTO> asignarRecolecciones(
            @PathVariable Long id,
            @RequestBody List<Long> recoleccionesIds
    ) {
        RutaRecoleccionDTO rutaActualizada = rutaService.asignarRecolecciones(id, recoleccionesIds);
        return ResponseEntity.ok(rutaActualizada);
    }

    // ===========================
    // ELIMINAR RUTA
    // ===========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
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

package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.CrearRutaDTO;
import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaRecoleccionController {

    private final RutaRecoleccionService rutaService;
    private final UsuarioRepository usuarioRepository;

    // ========== CRUD BÁSICO ==========
    @PostMapping
    public ResponseEntity<RutaRecoleccionDTO> crearRuta(@Valid @RequestBody CrearRutaDTO dto) {
        UsuarioEntity recolector = obtenerUsuarioAutenticado();
        return ResponseEntity.ok(rutaService.crearRuta(dto, recolector.getIdUsuario()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> actualizarRuta(@PathVariable Long id, @RequestBody RutaRecoleccionDTO dto) {
        return ResponseEntity.ok(rutaService.actualizarRuta(id, dto));
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

    // ========== ACCIONES DE ESTADO ==========
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<RutaRecoleccionDTO> iniciarRuta(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.iniciarRuta(id));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<RutaRecoleccionDTO> finalizarRuta(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.finalizarRuta(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<RutaRecoleccionDTO> cancelarRuta(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.cancelarRuta(id));
    }

    // ========== ELIMINACIÓN FÍSICA (solo admin) ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaService.eliminarFisicamente(id);
        return ResponseEntity.noContent().build();
    }

    // ========== REPORTES ==========
    @GetMapping("/export/excel")
    public void exportarExcel(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long recolectorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=rutas.xlsx");

        rutaService.generarReporteExcel(nombre, estado, recolectorId, fechaDesde, fechaHasta, response.getOutputStream());
    }

    @GetMapping("/export/pdf")
    public void exportarPDF(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long recolectorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=rutas.pdf");

        rutaService.generarReportePDF(nombre, estado, recolectorId, fechaDesde, fechaHasta, response.getOutputStream());
    }

    // ========== AUXILIAR ==========
    private UsuarioEntity obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        return usuarioRepository.findByCorreoAndEstadoTrue(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado o inactivo"));
    }
}
package com.EcoSoftware.Scrum6.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudRecoleccionController {

    @Autowired
    private SolicitudRecoleccionService solicitudService;

    public SolicitudRecoleccionController(SolicitudRecoleccionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // ========================================================
    // CREAR SOLICITUD - ID del usuario se obtiene del token
    // ========================================================
    @PostMapping
    public ResponseEntity<SolicitudRecoleccionDTO> crearSolicitud(@RequestBody SolicitudRecoleccionDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoUsuario = auth.getName();

        return ResponseEntity.ok(solicitudService.crearSolicitudConUsuario(dto, correoUsuario));
    }

    // ========================================================
    // Subir Evidencia
    // ========================================================
    @PostMapping(value = "/{id}/evidencia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirEvidencia(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {
            return ResponseEntity.ok(solicitudService.subirEvidencia(file, id));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir la evidencia");
        }
    }

    // ========================================================
    // OBTENER SOLICITUD POR ID
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    // ========================================================
    // LISTAR TODAS LAS SOLICITUDES (ADMIN)
    // ========================================================
    @GetMapping
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarTodas() {
        return ResponseEntity.ok(solicitudService.listarTodas());
    }

    @GetMapping("/idUsuario/{id}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorIdUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.listarPorUsuario(id));
    }

    // ========================================================
    // LISTAR SOLICITUDES POR ESTADO
    // ========================================================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorEstado(@PathVariable EstadoPeticion estado) {
        return ResponseEntity.ok(solicitudService.listarPorEstado(estado));
    }

    // ========================================================
    // LISTAR SOLICITUDES POR USUARIO
    // ========================================================
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.listarPorUsuario(id));
    }

    // ========================================================
    // LISTAR SOLICITUDES POR USUARIO Y ESTADO
    // ========================================================
    @GetMapping("/usuario/{id}/estado/{estado}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorUsuarioYEstado(
            @PathVariable Long id,
            @PathVariable EstadoPeticion estado) {

        return ResponseEntity.ok(solicitudService.listarPorUsuarioYEstado(id, estado));
    }

    // ========================================================
    // ACEPTAR SOLICITUD
    // ========================================================
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<SolicitudRecoleccionDTO> aceptarSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.aceptarSolicitud(id));
    }

    // ========================================================
    // RECHAZAR SOLICITUD
    // ========================================================
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudRecoleccionDTO> rechazarSolicitud(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String motivo) {

        return ResponseEntity.ok(solicitudService.rechazarSolicitud(id, motivo));
    }

    // ========================================================
    // ACTUALIZAR SOLICITUD
    // ========================================================
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> actualizarSolicitud(
            @PathVariable Long id,
            @RequestBody SolicitudRecoleccionDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoUsuario = auth.getName();

        dto.setIdSolicitud(id);

        return ResponseEntity.ok(solicitudService.actualizarSolicitudConUsuario(dto, correoUsuario));
    }

    // ========================================================
    // EXPORTAR A EXCEL
    // ========================================================
    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            HttpServletResponse response) throws IOException {

        EstadoPeticion estadoEnum = parseEstado(estado);
        Localidad localidadEnum = parseLocalidad(localidad);
        LocalDateTime inicio = parseFechaInicio(fechaDesde);
        LocalDateTime fin = parseFechaFin(fechaHasta);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");

        solicitudService.generarReporteExcel(estadoEnum, localidadEnum, inicio, fin, response.getOutputStream());
    }

    // ========================================================
    // EXPORTAR A PDF
    // ========================================================
    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            HttpServletResponse response) throws IOException, DocumentException {

        EstadoPeticion estadoEnum = parseEstado(estado);
        Localidad localidadEnum = parseLocalidad(localidad);
        LocalDateTime inicio = parseFechaInicio(fechaDesde);
        LocalDateTime fin = parseFechaFin(fechaHasta);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");

        solicitudService.generarReportePDF(estadoEnum, localidadEnum, inicio, fin, response.getOutputStream());
    }

    // ========================================================
    // MÉTODOS AUXILIARES
    // ========================================================
    private EstadoPeticion parseEstado(String estado) {
        if (estado == null || estado.isBlank()) return null;
        for (EstadoPeticion ep : EstadoPeticion.values()) {
            if (ep.name().equalsIgnoreCase(estado.trim())) return ep;
        }
        return null;
    }

    private Localidad parseLocalidad(String localidad) {
        if (localidad == null || localidad.isBlank()) return null;
        for (Localidad l : Localidad.values()) {
            if (l.name().equalsIgnoreCase(localidad.trim())) return l;
        }
        return null;
    }

    private LocalDateTime parseFechaInicio(String fechaDesde) {
        if (fechaDesde == null || fechaDesde.isBlank()) return null;
        LocalDate d = LocalDate.parse(fechaDesde);
        return d.atStartOfDay();
    }

    private LocalDateTime parseFechaFin(String fechaHasta) {
        if (fechaHasta == null || fechaHasta.isBlank()) return null;
        LocalDate d = LocalDate.parse(fechaHasta);
        return LocalDateTime.of(d, LocalTime.MAX);
    }
}

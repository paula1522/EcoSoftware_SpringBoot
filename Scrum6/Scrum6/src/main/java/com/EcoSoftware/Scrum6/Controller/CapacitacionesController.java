package com.EcoSoftware.Scrum6.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.CapacitacionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.InscripcionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ModuloDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ProgresoDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.UploadResultDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;
import com.EcoSoftware.Scrum6.Exception.ValidacionCapacitacionException;
import com.EcoSoftware.Scrum6.Service.CapacitacionesService;

@RestController
@RequestMapping("/api/capacitaciones")
public class CapacitacionesController {

    @Autowired
    private CapacitacionesService capacitacionesService;

    // CRUD simple
    @PostMapping
    public ResponseEntity<CapacitacionDTO> crearCapacitacion(@RequestBody CapacitacionDTO dto) {
        return ResponseEntity.ok(capacitacionesService.crearCapacitacion(dto));
    }

    @PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> subirImagen(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file) {

    try {
        String url = capacitacionesService.subirImagen(file, id);
        return ResponseEntity.ok(Map.of("url", url));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @PutMapping("/{id}")
    public ResponseEntity<CapacitacionDTO> actualizarCapacitacion(@PathVariable Long id,
                                                                  @RequestBody CapacitacionDTO dto) {
        return ResponseEntity.ok(capacitacionesService.actualizarCapacitacion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCapacitacion(@PathVariable Long id) {
        capacitacionesService.eliminarCapacitacion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapacitacionDTO> obtenerCapacitacion(@PathVariable Long id) {
        return ResponseEntity.ok(capacitacionesService.obtenerCapacitacionPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CapacitacionDTO>> listarCapacitaciones() {
        return ResponseEntity.ok(capacitacionesService.listarTodasCapacitaciones());
    }

    // ===========================
    // EXCEL: Validar y Cargar
    // ===========================
    @PostMapping("/validar-excel")
    public ResponseEntity<List<CapacitacionDTO>> validarCapacitacionesExcel(@RequestParam("file") MultipartFile file) {
        List<CapacitacionDTO> duplicadas = capacitacionesService.validarCapacitacionesExcel(file);
        return ResponseEntity.ok(duplicadas);
    }

    @PostMapping("/cargar-excel")
    public ResponseEntity<?> cargarDesdeExcel(@RequestParam("file") MultipartFile file) {
        try {
            UploadResultDTO resultado = capacitacionesService.cargarCapacitacionesDesdeExcel(file);
            return ResponseEntity.ok(resultado);
        } catch (ValidacionCapacitacionException ex) {
            // Si la excepción ocurre, devolvemos el mensaje y la lista de duplicadas
            Map<String, Object> body = new HashMap<>();
            body.put("message", ex.getMessage());
            body.put("duplicadas", ex.getDuplicadas());
            return ResponseEntity.badRequest().body(body);
        } catch (RuntimeException ex) {
            // errores generales
            Map<String, Object> body = new HashMap<>();
            body.put("message", ex.getMessage());
            return ResponseEntity.status(500).body(body);
        }
    }

    @GetMapping("/plantilla")
    public ResponseEntity<byte[]> descargarPlantilla() {
        byte[] archivo = capacitacionesService.generarPlantillaExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_capacitaciones.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo);
    }

@GetMapping("/mis-capacitaciones/{usuarioId}")
public ResponseEntity<List<CapacitacionDTO>> obtenerMisCapacitaciones(@PathVariable Long usuarioId) {

    return ResponseEntity.ok(
            capacitacionesService.obtenerCapacitacionesUsuario(usuarioId)
    );
}

    // ========== MÓDULOS ==========
    @PostMapping("/modulos")
    public ResponseEntity<ModuloDTO> crearModulo(@RequestBody ModuloDTO dto) {
        return ResponseEntity.ok(capacitacionesService.crearModulo(dto));
    }

    @PutMapping("/modulos/{id}")
    public ResponseEntity<ModuloDTO> actualizarModulo(@PathVariable Long id, @RequestBody ModuloDTO dto) {
        return ResponseEntity.ok(capacitacionesService.actualizarModulo(id, dto));
    }

    @DeleteMapping("/modulos/{id}")
    public ResponseEntity<Void> eliminarModulo(@PathVariable Long id) {
        capacitacionesService.eliminarModulo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{capacitacionId}/modulos")
    public ResponseEntity<List<ModuloDTO>> listarModulosPorCapacitacion(@PathVariable Long capacitacionId) {
        return ResponseEntity.ok(capacitacionesService.listarModulosPorCapacitacion(capacitacionId));
    }

    @GetMapping("/modulos/plantilla")
    public ResponseEntity<byte[]> descargarPlantillaModulos() {
        byte[] archivo = capacitacionesService.generarPlantillaModulosExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_modulos.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo);
    }

    @PostMapping("/{capacitacionId}/modulos/cargar-excel")
    public ResponseEntity<String> cargarModulosDesdeExcel(
            @PathVariable Long capacitacionId,
            @RequestParam("file") MultipartFile file) {
        capacitacionesService.cargarModulosDesdeExcel(capacitacionId, file);
        return ResponseEntity.ok("Módulos cargados correctamente para la capacitación " + capacitacionId);
    }

    // ========== INSCRIPCIONES ==========
    @PostMapping("/inscripciones")
    public ResponseEntity<InscripcionDTO> inscribirse(@RequestParam Long usuarioId, @RequestParam Long cursoId) {
        return ResponseEntity.ok(capacitacionesService.inscribirse(usuarioId, cursoId));
    }

    @PutMapping("/inscripciones/{id}")
    public ResponseEntity<InscripcionDTO> actualizarEstadoInscripcion(@PathVariable Long id,
                                                                      @RequestParam EstadoCurso estado) {
        return ResponseEntity.ok(capacitacionesService.actualizarEstadoInscripcion(id, estado));
    }

    @GetMapping("/inscripciones/usuario/{usuarioId}")
    public ResponseEntity<List<InscripcionDTO>> listarInscripcionesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(capacitacionesService.listarInscripcionesPorUsuario(usuarioId));
    }

    @GetMapping("/inscripciones/curso/{cursoId}")
    public ResponseEntity<List<InscripcionDTO>> listarInscripcionesPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(capacitacionesService.listarInscripcionesPorCurso(cursoId));
    }

    // ========== PROGRESO ==========
    @PostMapping("/progreso")
    public ResponseEntity<ProgresoDTO> registrarProgreso(@RequestBody ProgresoDTO dto) {
        return ResponseEntity.ok(capacitacionesService.registrarProgreso(dto));
    }

    @PutMapping("/progreso/{id}")
    public ResponseEntity<ProgresoDTO> actualizarProgreso(@PathVariable Long id, @RequestBody ProgresoDTO dto) {
        return ResponseEntity.ok(capacitacionesService.actualizarProgreso(id, dto));
    }

    @GetMapping("/progreso/usuario/{usuarioId}")
    public ResponseEntity<List<ProgresoDTO>> listarProgresosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(capacitacionesService.listarProgresosPorUsuario(usuarioId));
    }

    @GetMapping("/progreso/curso/{cursoId}")
    public ResponseEntity<List<ProgresoDTO>> listarProgresosPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(capacitacionesService.listarProgresosPorCurso(cursoId));
    }
}

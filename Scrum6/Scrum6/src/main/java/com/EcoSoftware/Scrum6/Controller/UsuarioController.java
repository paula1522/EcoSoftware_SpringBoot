package com.EcoSoftware.Scrum6.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Service.UsuarioService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personas")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

// ============================================
    //  DESCARGAR PLANTILLA POR ROL
    // ============================================
    @GetMapping("/plantilla/{rol}")
    public ResponseEntity<byte[]> descargarPlantilla(@PathVariable String rol) {

        byte[] plantilla = usuarioService.generarPlantillaExcelPorRol(rol);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=plantilla_" + rol + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(plantilla);
    }

    // ============================================
    //  CARGAR EXCEL POR ROL
    // ============================================
    @PostMapping("/cargar/{rol}")
    public ResponseEntity<?> cargarExcelPorRol(
            @PathVariable String rol,
            @RequestParam("archivo") MultipartFile archivo) {

        try {
            List<String> errores = usuarioService.cargarUsuariosDesdeExcel(rol, archivo);

            if (errores.isEmpty()) {
                return ResponseEntity.ok("Usuarios cargados correctamente");
            }

            return ResponseEntity.badRequest().body(errores);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error cargando archivo: " + e.getMessage());
        }
    }



    // ENDPOINT DE PRUEBA - ELIMINAR LUEGO
    @PostMapping("/test-registro")
    public ResponseEntity<String> testRegistro(@RequestBody String testData) {
        System.out.println("=== TEST REGISTRO RECIBIDO ===");
        System.out.println("Datos: " + testData);
        return ResponseEntity.ok("Test registro exitoso: " + testData);
    }


    // ENDPOINT DE PRUEBA 2
    @GetMapping("/test-public")
    public ResponseEntity<String> testPublic() {
        return ResponseEntity.ok("Endpoint público funciona OK");
    }


    //Trae todos los usuarios registrados al cargar el modulo usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarPersonas() {
        List<UsuarioDTO> personas = usuarioService.listarUsuarios();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }

    //Trae usuarios por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Filtra correo por exactitud y/o coincidencia
    @GetMapping("/filtrar-correo")
    public ResponseEntity<List<UsuarioDTO>> obtenerCorreo(@RequestParam String correo) {
        List<UsuarioDTO> usuarios = usuarioService.encontrarPorCorreo(correo);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    //Filtra documento (NIT o CEDULA) por exactitud y/o coincidencia
    @GetMapping("/filtrar-documento")
    public ResponseEntity<List<UsuarioDTO>> obtenerDocumento(@RequestParam String documento) {
        List<UsuarioDTO> usuarios = usuarioService.encontrarPorDocumento(documento);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    //Filtra nombre por exactitud y/o coincidencia
    @GetMapping("/filtrar-nombre")
    public ResponseEntity<List<UsuarioDTO>> obtenerNombre(@RequestParam String nombre) {
        List<UsuarioDTO> usuarios = usuarioService.encontrarPorNombre(nombre);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    //Registra un nuevo usuario

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> insertarUsuario(@Valid @RequestBody UsuarioDTO usuario) {
        try {
            System.out.println("=== REGISTRO REAL RECIBIDO ===");
            System.out.println("Usuario: " + usuario.getNombre() + " - " + usuario.getCorreo());
            UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            System.out.println("Error en registro: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

// Listar pendientes
@GetMapping("/pendientes")
public ResponseEntity<List<UsuarioDTO>> listarPendientes() {
    return ResponseEntity.ok(usuarioService.listarUsuariosPendientes());
}

// Contador de pendientes
@GetMapping("/pendientes/count")
public ResponseEntity<Long> contarPendientes() {
    return ResponseEntity.ok(usuarioService.contarUsuariosPendientes());
}

// Aprobar
@PatchMapping("/aprobar/{id}")
public ResponseEntity<String> aprobarUsuario(@PathVariable Long id) {
    try {
        usuarioService.aprobarUsuario(id);
        return ResponseEntity.ok("Usuario aprobado correctamente");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

// Rechazar/eliminar
@DeleteMapping("/rechazar/{id}")
public ResponseEntity<String> rechazarUsuario(@PathVariable Long id) {
    try {
        usuarioService.rechazarUsuario(id);
        return ResponseEntity.ok("Usuario rechazado y eliminado correctamente");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

// Subir documento para un usuario
@PostMapping(value = "/{id}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> subirDocumento(
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file,
        @RequestParam("tipo") String tipo) {

    try {
        String url = usuarioService.subirDocumento(file, id, tipo);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Documento subido correctamente",
                "url", url
        ));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
}



    //Actualiza un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEditarDTO> actualizarUsuario(@Valid @RequestBody UsuarioEditarDTO usuarioDTO, @PathVariable Long id) {
        try{
            UsuarioEditarDTO actualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminacionPorEstado(@PathVariable Long id){
        try{
            usuarioService.eliminacionPorEstado(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Backend OK");
    }

    //Metodo de eliminado en base de datos, activar solo si no es suficiente eliminacion logica
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try{usuarioService.eliminarPersona(id);
        return ResponseEntity.ok("Usuario eliminado");
        } catch (Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

    }
}
    // ================================
    // EXPORTACIONES
    // ================================

    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String documento,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.xlsx");
        usuarioService.exportUsuariosToExcel(nombre, correo, documento, response.getOutputStream());
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String documento,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");
        usuarioService.exportUsuariosToPDF(nombre, correo, documento, response.getOutputStream());
    }

    @GetMapping("/graficos/usuarios-localidad-rol")
    public ResponseEntity<Map<String, Map<String, Long>>> graficoLocalidadesRoles() {
        Map<String, Map<String, Long>> datos = usuarioService.obtenerUsuariosPorLocalidadYRol();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/estadisticas/barrios-localidades")
    public ResponseEntity<List<Object[]>> obtenerUsuariosPorBarriosLocalidades() {
        List<Object[]> datos = usuarioService.obtenerUsuariosPorBarrioYLocalidad();
        return ResponseEntity.ok(datos);
    }

}


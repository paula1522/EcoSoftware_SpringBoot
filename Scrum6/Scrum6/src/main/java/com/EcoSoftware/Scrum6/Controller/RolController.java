package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RolDTO;
import com.EcoSoftware.Scrum6.Service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")

public class RolController {

    @Autowired
    private RolService rolService;

    // Listar todos los roles
    @GetMapping
    public ResponseEntity<List<RolDTO>> listarRoles() {
        List<RolDTO> roles = rolService.listarRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // Crear un nuevo rol
    @PostMapping("/crear")

    public ResponseEntity<RolDTO> crearRol(@RequestBody RolDTO rolDTO) {
        RolDTO nuevoRol = rolService.crearRol(rolDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }

    // Actualizar un rol existente
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizarRol(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        RolDTO actualizado = rolService.actualizarRol(id, rolDTO);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar un rol
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Long id) {
        rolService.eliminarRol(id);
        return ResponseEntity.ok("Rol eliminado correctamente");
    }
}

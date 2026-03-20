package com.EcoSoftware.Scrum6.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMaterialMesDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMaterialDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMesDTO;
import com.EcoSoftware.Scrum6.Service.EmpresaDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empresa/dashboard")
@RequiredArgsConstructor
public class EmpresaDashboardController {

    private final EmpresaDashboardService empresaDashboardService;

    @GetMapping("/solicitudes-estado")
    public ResponseEntity<List<EmpresaDashboardEstadoDTO>> obtenerSolicitudesPorEstado(Authentication authentication) {
        return ResponseEntity.ok(empresaDashboardService.obtenerSolicitudesPorEstado(authentication.getName()));
    }

    @GetMapping("/solicitudes-mensuales")
    public ResponseEntity<List<EmpresaDashboardMesDTO>> obtenerSolicitudesMensuales(Authentication authentication) {
        return ResponseEntity.ok(empresaDashboardService.obtenerSolicitudesMensuales(authentication.getName()));
    }

    @GetMapping("/materiales-tipo")
    public ResponseEntity<List<EmpresaDashboardMaterialDTO>> obtenerMaterialesPorTipo(Authentication authentication) {
        return ResponseEntity.ok(empresaDashboardService.obtenerMaterialesPorTipo(authentication.getName()));
    }

    @GetMapping("/material-gestionado-mes")
    public ResponseEntity<List<EmpresaDashboardMaterialMesDTO>> obtenerMaterialGestionadoPorMes(Authentication authentication) {
        return ResponseEntity.ok(empresaDashboardService.obtenerMaterialGestionadoPorMes(authentication.getName()));
    }
}
package com.EcoSoftware.Scrum6.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardImpactoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardResiduoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardTiempoDTO;
import com.EcoSoftware.Scrum6.Service.CiudadanoDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ciudadano/dashboard")
@RequiredArgsConstructor
public class CiudadanoDashboardController {

    private final CiudadanoDashboardService ciudadanoDashboardService;

    @GetMapping("/solicitudes-estado")
    public ResponseEntity<List<CiudadanoDashboardEstadoDTO>> obtenerSolicitudesPorEstado(Authentication authentication) {
        return ResponseEntity.ok(ciudadanoDashboardService.obtenerSolicitudesPorEstado(authentication.getName()));
    }

    @GetMapping("/solicitudes-tiempo")
    public ResponseEntity<List<CiudadanoDashboardTiempoDTO>> obtenerSolicitudesPorTiempo(Authentication authentication) {
        return ResponseEntity.ok(ciudadanoDashboardService.obtenerSolicitudesPorTiempo(authentication.getName()));
    }

    @GetMapping("/residuos-por-tipo")
    public ResponseEntity<List<CiudadanoDashboardResiduoDTO>> obtenerResiduosPorTipo(Authentication authentication) {
        return ResponseEntity.ok(ciudadanoDashboardService.obtenerResiduosPorTipo(authentication.getName()));
    }

    @GetMapping("/impacto-ambiental")
    public ResponseEntity<List<CiudadanoDashboardImpactoDTO>> obtenerImpactoAmbiental(Authentication authentication) {
        return ResponseEntity.ok(ciudadanoDashboardService.obtenerImpactoAmbiental(authentication.getName()));
    }
}

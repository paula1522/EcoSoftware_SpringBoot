package com.EcoSoftware.Scrum6.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardCumplimientoDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardMaterialDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardPeriodoDTO;
import com.EcoSoftware.Scrum6.Service.RecicladorDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reciclador/dashboard")
@RequiredArgsConstructor
public class RecicladorDashboardController {

    private final RecicladorDashboardService recicladorDashboardService;

    @GetMapping("/recolecciones-periodo")
    public ResponseEntity<List<RecicladorDashboardPeriodoDTO>> obtenerRecoleccionesPorPeriodo(Authentication authentication) {
        return ResponseEntity.ok(recicladorDashboardService.obtenerRecoleccionesPorPeriodo(authentication.getName()));
    }

    @GetMapping("/recolecciones-estado")
    public ResponseEntity<List<RecicladorDashboardEstadoDTO>> obtenerRecoleccionesPorEstado(Authentication authentication) {
        return ResponseEntity.ok(recicladorDashboardService.obtenerRecoleccionesPorEstado(authentication.getName()));
    }

    @GetMapping("/cumplimiento")
    public ResponseEntity<List<RecicladorDashboardCumplimientoDTO>> obtenerCumplimiento(Authentication authentication) {
        return ResponseEntity.ok(recicladorDashboardService.obtenerCumplimiento(authentication.getName()));
    }

    @GetMapping("/material-recolectado")
    public ResponseEntity<List<RecicladorDashboardMaterialDTO>> obtenerMaterialRecolectado(Authentication authentication) {
        return ResponseEntity.ok(recicladorDashboardService.obtenerMaterialRecolectado(authentication.getName()));
    }
}

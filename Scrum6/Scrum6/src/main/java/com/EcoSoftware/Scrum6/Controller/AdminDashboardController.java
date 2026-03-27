package com.EcoSoftware.Scrum6.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.DTO.AdminDashboardLocalidadDTO;
import com.EcoSoftware.Scrum6.Service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/usuarios-pendientes")
    public ResponseEntity<Long> obtenerUsuariosPendientes() {
        return ResponseEntity.ok(adminDashboardService.obtenerUsuariosPendientes());
    }

    @GetMapping("/total-capacitaciones")
    public ResponseEntity<Long> obtenerTotalCapacitaciones() {
        return ResponseEntity.ok(adminDashboardService.obtenerTotalCapacitaciones());
    }

    @GetMapping("/solicitudes-localidad")
    public ResponseEntity<List<AdminDashboardLocalidadDTO>> obtenerSolicitudesPorLocalidad() {
        return ResponseEntity.ok(adminDashboardService.obtenerSolicitudesPorLocalidad());
    }
}

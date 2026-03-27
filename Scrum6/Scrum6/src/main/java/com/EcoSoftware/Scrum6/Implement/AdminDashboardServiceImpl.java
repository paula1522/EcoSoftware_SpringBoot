package com.EcoSoftware.Scrum6.Implement;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EcoSoftware.Scrum6.DTO.AdminDashboardLocalidadDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;
import com.EcoSoftware.Scrum6.Repository.CapacitacionRepository;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final SolicitudRecoleccionRepository solicitudRecoleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CapacitacionRepository capacitacionRepository;

    @Override
    public Long obtenerUsuariosPendientes() {
        Long pendientes = usuarioRepository.countByEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR);
        return pendientes == null ? 0L : pendientes;
    }

    @Override
    public Long obtenerTotalCapacitaciones() {
        return capacitacionRepository.count();
    }

    @Override
    public List<AdminDashboardLocalidadDTO> obtenerSolicitudesPorLocalidad() {
        List<Object[]> filas = solicitudRecoleccionRepository.obtenerSolicitudesPorLocalidad();
        return filas.stream()
                .map(row -> new AdminDashboardLocalidadDTO(
                        row[0] == null ? "Localidad desconocida" : row[0].toString(),
                        ((Number) row[1]).longValue()))
                .toList();
    }

}

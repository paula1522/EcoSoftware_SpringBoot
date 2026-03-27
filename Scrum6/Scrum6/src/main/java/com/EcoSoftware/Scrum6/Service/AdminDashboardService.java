package com.EcoSoftware.Scrum6.Service;

import java.util.List;

import com.EcoSoftware.Scrum6.DTO.AdminDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.AdminDashboardLocalidadDTO;

public interface AdminDashboardService {
    Long obtenerUsuariosPendientes();

    Long obtenerTotalCapacitaciones();

    List<AdminDashboardLocalidadDTO> obtenerSolicitudesPorLocalidad();
}

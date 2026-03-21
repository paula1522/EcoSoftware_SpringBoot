package com.EcoSoftware.Scrum6.Service;

import java.util.List;

import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardCumplimientoDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardMaterialDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardPeriodoDTO;

public interface RecicladorDashboardService {
    List<RecicladorDashboardPeriodoDTO> obtenerRecoleccionesPorPeriodo(String correoReciclador);

    List<RecicladorDashboardEstadoDTO> obtenerRecoleccionesPorEstado(String correoReciclador);

    List<RecicladorDashboardCumplimientoDTO> obtenerCumplimiento(String correoReciclador);

    List<RecicladorDashboardMaterialDTO> obtenerMaterialRecolectado(String correoReciclador);
}

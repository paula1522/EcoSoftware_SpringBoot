package com.EcoSoftware.Scrum6.Service;

import java.util.List;

import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardImpactoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardResiduoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardTiempoDTO;

public interface CiudadanoDashboardService {
    List<CiudadanoDashboardEstadoDTO> obtenerSolicitudesPorEstado(String correoCiudadano);

    List<CiudadanoDashboardTiempoDTO> obtenerSolicitudesPorTiempo(String correoCiudadano);

    List<CiudadanoDashboardResiduoDTO> obtenerResiduosPorTipo(String correoCiudadano);

    List<CiudadanoDashboardImpactoDTO> obtenerImpactoAmbiental(String correoCiudadano);
}

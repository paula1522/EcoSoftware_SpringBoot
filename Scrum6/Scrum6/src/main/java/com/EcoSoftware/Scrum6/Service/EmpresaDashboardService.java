package com.EcoSoftware.Scrum6.Service;

import java.util.List;

import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMaterialMesDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMaterialDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMesDTO;

public interface EmpresaDashboardService {
    List<EmpresaDashboardEstadoDTO> obtenerSolicitudesPorEstado(String correoEmpresa);

    List<EmpresaDashboardMesDTO> obtenerSolicitudesMensuales(String correoEmpresa);

    List<EmpresaDashboardMaterialDTO> obtenerMaterialesPorTipo(String correoEmpresa);

    List<EmpresaDashboardMaterialMesDTO> obtenerMaterialGestionadoPorMes(String correoEmpresa);
}
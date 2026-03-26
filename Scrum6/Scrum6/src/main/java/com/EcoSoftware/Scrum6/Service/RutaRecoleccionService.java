package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;

import java.util.List;

public interface RutaRecoleccionService {

    RutaRecoleccionDTO crearRuta(RutaRecoleccionDTO dto, Long recolectorId);

    RutaRecoleccionDTO obtenerPorId(Long id);

    List<RutaRecoleccionDTO> listarTodas();

    List<RutaRecoleccionDTO> listarPorEstado(EstadoRuta estado);

    List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId);

    RutaRecoleccionDTO iniciarRuta(Long id);

    RutaRecoleccionDTO finalizarRuta(Long id);
}
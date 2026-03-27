package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.CrearRutaDTO;
import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface RutaRecoleccionService {
    RutaRecoleccionDTO crearRuta(CrearRutaDTO dto, Long recolectorId);
    RutaRecoleccionDTO actualizarRuta(Long id, RutaRecoleccionDTO dto);
    RutaRecoleccionDTO obtenerPorId(Long id);
    List<RutaRecoleccionDTO> listarTodas();
    List<RutaRecoleccionDTO> listarPorEstado(EstadoRuta estado);
    List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId);
    RutaRecoleccionDTO iniciarRuta(Long id);
    RutaRecoleccionDTO finalizarRuta(Long id);
    
    // Nuevos métodos
    void eliminarFisicamente(Long id);     // Eliminación real
    RutaRecoleccionDTO cancelarRuta(Long id); // Eliminación lógica (cambia estado a CANCELADA)
    
    // Reportes
    void generarReporteExcel(String nombre, String estado, Long recolectorId,
                         LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                         OutputStream os) throws IOException;

void generarReportePDF(String nombre, String estado, Long recolectorId,
                       LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                       OutputStream os) throws IOException;
}
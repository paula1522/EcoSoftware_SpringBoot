package com.EcoSoftware.Scrum6.Service;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.itextpdf.text.DocumentException;

public interface SolicitudRecoleccionService {

    // CREAR
    SolicitudRecoleccionDTO crearSolicitud(SolicitudRecoleccionDTO dto, String correoUsuario);

    // CRUD
    SolicitudRecoleccionDTO obtenerPorId(Long id);
    List<SolicitudRecoleccionDTO> listarTodas();

    List<SolicitudRecoleccionDTO> listarPorUsuario(Long usuarioId);
    List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado);
    List<SolicitudRecoleccionDTO> listarPorUsuarioYEstado(Long usuarioId, EstadoPeticion estado);

    // ACCIONES
    SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId);

    SolicitudRecoleccionDTO rechazarSolicitud(Long solicitudId, String motivo);

    SolicitudRecoleccionDTO cancelarSolicitud(Long solicitudId);

    // UPDATE (solo si está pendiente)
    SolicitudRecoleccionDTO actualizarSolicitud(Long id, SolicitudRecoleccionDTO dto, String correoUsuario);

    // ARCHIVOS
    String subirEvidencia(MultipartFile file, Long idSolicitud) throws IOException;

    // REPORTES
    void generarReporteExcel(EstadoPeticion estado,
                             Localidad localidad,
                             LocalDateTime fechaInicio,
                             LocalDateTime fechaFin,
                             OutputStream os) throws IOException;

    void generarReportePDF(EstadoPeticion estado,
                           Localidad localidad,
                           LocalDateTime fechaInicio,
                           LocalDateTime fechaFin,
                           OutputStream os) throws IOException, DocumentException;

    // DASHBOARD
    List<Object[]> obtenerRechazadasPorMotivo();
    Long contarAceptadas();
    Long contarPendientes();
    List<Object[]> obtenerSolicitudesPorLocalidad();
}
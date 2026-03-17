package com.EcoSoftware.Scrum6.Service;

// Importaciones necesarias para la interfaz del servicio
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.itextpdf.text.DocumentException;

/**
 * Interfaz que define los métodos del servicio de solicitudes de recolección.
 * Aquí solo se declaran las operaciones, la implementación se realiza en la clase que implemente esta interfaz.
 */
public interface SolicitudRecoleccionService {

    // Crear una nueva solicitud asociada a un usuario (estado inicial: Pendiente)
    SolicitudRecoleccionDTO crearSolicitudConUsuario(SolicitudRecoleccionDTO dto, String correoUsuario);

    //Subir evidencia de reciclaje
    String subirEvidencia(MultipartFile file, Long idSolicitud) throws IOException;

    // Obtener una solicitud específica por su ID
    SolicitudRecoleccionDTO obtenerPorId(Long id);

    // Listar todas las solicitudes registradas en el sistema
    List<SolicitudRecoleccionDTO> listarTodas();

    // Listar solicitudes filtradas por usuario y estado
    List<SolicitudRecoleccionDTO> listarPorUsuarioYEstado(Long usuarioId, EstadoPeticion estado);

    // Listar solicitudes filtradas por usuario
    List<SolicitudRecoleccionDTO> listarPorUsuario(Long usuarioId);


    // Listar solicitudes filtradas por estado (Pendiente, Aceptada o Rechazada)
    List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado);

    // Aceptar una solicitud pendiente y asignarle un recolector
    SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId);

    // Rechazar una solicitud pendiente, permitiendo registrar un motivo de rechazo
    SolicitudRecoleccionDTO rechazarSolicitud(Long solicitudId, String motivo);

    // Actualizar datos de una solicitud (solo permitido si está en estado Pendiente)
    SolicitudRecoleccionDTO actualizarSolicitudConUsuario(SolicitudRecoleccionDTO dto, String correoUsuario);

    // Generar un reporte en formato Excel filtrando por estado, localidad y rango de fechas
    void generarReporteExcel(EstadoPeticion estado,
                             Localidad localidad,
                             LocalDateTime fechaInicio,
                             LocalDateTime fechaFin,
                             OutputStream os) throws IOException;

    // Generar un reporte en formato PDF filtrando por estado, localidad y rango de fechas
    void generarReportePDF(EstadoPeticion estado,
                           Localidad localidad,
                           LocalDateTime fechaInicio,
                           LocalDateTime fechaFin,
                           OutputStream os) throws IOException, DocumentException;

    // Obtener estadísticas de solicitudes rechazadas agrupadas por motivo de rechazo
    List<Object[]> obtenerRechazadasPorMotivo();

    // Contar solicitudes aceptadas
    Long contarAceptadas();

    // Contar solicitudes pendientes
    Long contarPendientes();

    // Obtener solicitudes agrupadas por localidad
    List<Object[]> obtenerSolicitudesPorLocalidad();


}

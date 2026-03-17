package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;

import java.util.List;

public interface RecoleccionService {

    // Buscar recolección por ID
    RecoleccionDTO obtenerPorId(Long id);

    // Listar todas las recolecciones activas
    List<RecoleccionDTO> listarActivas();

    // Listar recolecciones activas de un recolector
    List<RecoleccionDTO> listarPorRecolector(Long recolectorId);

    // Listar recolecciones activas de una ruta
    List<RecoleccionDTO> listarPorRuta(Long rutaId);

    // Recolecciones que NO están asignadas a ninguna ruta y están activas
    List<RecoleccionDTO> listarSinRutaPorRecolector(Long recolectorId);


    // Actualizar estado de recolección (ej: Pendiente → Completada → Cancelada)
    RecoleccionDTO actualizarEstado(Long recoleccionId, EstadoRecoleccion nuevoEstado);

    // Actualizar datos de recoleccion
    RecoleccionDTO actualizarRecoleccion(Long id, RecoleccionDTO dto);

    List<RecoleccionDTO> listarTodasRecolector(Long recolectorId);
    // Eliminar lógicamente una recolección (activo = false)
    void eliminarLogicamente(Long recoleccionId);

    List<RecoleccionDTO> listarTodas();
}



package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.DTO.CrearRutaRequestDTO;

import java.util.List;

public interface RutaRecoleccionService {
    // Crear una nueva ruta para un recolector
    RutaRecoleccionDTO crearRuta(CrearRutaRequestDTO dto);

    // Obtener ruta por ID
    RutaRecoleccionDTO obtenerPorId(Long rutaId);

    // Listar todas las rutas
    List<RutaRecoleccionDTO> listarTodas();

    // Listar rutas de un recolector
    List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId);

    // Listar rutas por estado
    List<RutaRecoleccionDTO> listarPorEstado(EstadoRuta estado);

    // Cambiar estado de la ruta
    RutaRecoleccionDTO actualizarEstado(Long rutaId, EstadoRuta nuevoEstado);

    // Asignar recolecciones a la ruta
    RutaRecoleccionDTO asignarRecolecciones(Long rutaId, List<Long> recoleccionesIds);

    // Eliminar ruta (opcional: lógica si está PLANIFICADA)
    void eliminarRuta(Long rutaId);
}

package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.*;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


/**
 * Interfaz de servicios para el módulo de capacitaciones
 */
public interface CapacitacionesService {

    // ===========================
    // Capacitacion
    // ===========================
    CapacitacionDTO crearCapacitacion(CapacitacionDTO dto);
    
    String subirImagen(MultipartFile file, Long capacitacionId) throws Exception;

    CapacitacionDTO actualizarCapacitacion(Long id, CapacitacionDTO dto);

    void eliminarCapacitacion(Long id);

    CapacitacionDTO obtenerCapacitacionPorId(Long id);

    List<CapacitacionDTO> listarTodasCapacitaciones();

    List<CapacitacionDTO> obtenerCapacitacionesUsuario(Long usuarioId);

    // ===========================
    // CARGA MASIVA DE CAPACITACIONES
    // ===========================
    byte[] generarPlantillaExcel();

    UploadResultDTO cargarCapacitacionesDesdeExcel(MultipartFile file);

    List<CapacitacionDTO> validarCapacitacionesExcel(MultipartFile file);


    // ===========================
    // MÓDULOS
    // ===========================
    ModuloDTO crearModulo(ModuloDTO dto);

    //String subirMaterial(ModuloDTO dto, MultipartFile file, String tipoMaterial) throws Exception;

    ModuloDTO actualizarModulo(Long id, ModuloDTO dto);

    void eliminarModulo(Long id);

    List<ModuloDTO> listarModulosPorCapacitacion(Long capacitacionId);

    byte[] generarPlantillaModulosExcel();

    void cargarModulosDesdeExcel(Long capacitacionId, MultipartFile file);

    // ===========================
    // INSCRIPCIONES
    // ===========================
    InscripcionDTO inscribirse(Long usuarioId, Long cursoId);

    InscripcionDTO actualizarEstadoInscripcion(Long id, EstadoCurso estadoCurso);

    List<InscripcionDTO> listarInscripcionesPorUsuario(Long usuarioId);

    List<InscripcionDTO> listarInscripcionesPorCurso(Long cursoId);

    // ===========================
    // PROGRESO
    // ===========================
    ProgresoDTO registrarProgreso(ProgresoDTO dto);

    ProgresoDTO actualizarProgreso(Long id, ProgresoDTO dto);

    List<ProgresoDTO> listarProgresosPorUsuario(Long usuarioId);

    List<ProgresoDTO> listarProgresosPorCurso(Long cursoId);

    // ===========================
    // VALIDACIÓN DE CAPACITACIONES
    // ===========================
    boolean existeCapacitacionPorNombre(String nombre);

    boolean existeCapacitacionPorDescripcion(String descripcion);
}

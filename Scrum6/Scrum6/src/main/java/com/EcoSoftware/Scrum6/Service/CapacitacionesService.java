package com.EcoSoftware.Scrum6.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.*;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.CapacitacionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.EvaluacionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.InscripcionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.IntentoEvaluacionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ModuloDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ProgresoDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.UploadResultDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;

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

    String subirPdfModulo(MultipartFile file, Long moduloId) throws Exception;

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

    ProgresoDTO obtenerProgresoUsuarioPorCurso(Long usuarioId, Long cursoId);

    // ===========================
    // EVALUACIONES POR MÓDULO
    // ===========================
    EvaluacionDTO crearEvaluacion(EvaluacionDTO dto);

    EvaluacionDTO actualizarEvaluacion(Long id, EvaluacionDTO dto);

    void eliminarEvaluacion(Long id);

    List<EvaluacionDTO> listarEvaluacionesPorModulo(Long moduloId);

    IntentoEvaluacionDTO registrarIntentoEvaluacion(IntentoEvaluacionDTO dto);

    List<IntentoEvaluacionDTO> listarIntentosPorEvaluacionYUsuario(Long evaluacionId, Long usuarioId);

    // ===========================
    // VALIDACIÓN DE CAPACITACIONES
    // ===========================
    boolean existeCapacitacionPorNombre(String nombre);

    boolean existeCapacitacionPorDescripcion(String descripcion);
}

package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.CrearRutaRequestDTO;
import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutaRecoleccionServiceImpl implements RutaRecoleccionService {

    private final RutaRecoleccionRepository rutaRecoleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecoleccionRepository recoleccionRepository;

    // ========================================================
    // CREAR RUTA
    // ========================================================
    @Override
    @Transactional
    public RutaRecoleccionDTO crearRuta(CrearRutaRequestDTO dto) {
        UsuarioEntity recolector = usuarioRepository.findById(dto.getRecolectorId())
                .orElseThrow(() -> new EntityNotFoundException("Recolector no encontrado"));

        RutaRecoleccionEntity ruta = new RutaRecoleccionEntity();
        ruta.setRecolector(recolector);
        ruta.setEstado(EstadoRuta.PLANIFICADA);

        if (dto.getRecoleccionesIds() != null && !dto.getRecoleccionesIds().isEmpty()) {
            List<RecoleccionEntity> recolecciones = recoleccionRepository.findAllById(dto.getRecoleccionesIds());
            ruta.setRecolecciones(recolecciones);
        }

        rutaRecoleccionRepository.save(ruta);
        return convertirADTO(ruta);
    }

    // ========================================================
    // OBTENER RUTA POR ID
    // ========================================================
    @Override
    public RutaRecoleccionDTO obtenerPorId(Long rutaId) {
        RutaRecoleccionEntity ruta = rutaRecoleccionRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        return convertirADTO(ruta);
    }

    // ========================================================
    // LISTAR TODAS LAS RUTAS
    // ========================================================
    @Override
    public List<RutaRecoleccionDTO> listarTodas() {
        return rutaRecoleccionRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // LISTAR RUTAS POR RECOLECTOR
    // ========================================================
    @Override
    public List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId) {
        return rutaRecoleccionRepository.findByRecolector_IdUsuario(recolectorId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // LISTAR RUTAS POR ESTADO
    // ========================================================
    @Override
    public List<RutaRecoleccionDTO> listarPorEstado(EstadoRuta estado) {
        return rutaRecoleccionRepository.findByEstado(estado)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // ACTUALIZAR ESTADO DE LA RUTA
    // ========================================================
    @Override
    @Transactional
    public RutaRecoleccionDTO actualizarEstado(Long rutaId, EstadoRuta nuevoEstado) {
        RutaRecoleccionEntity ruta = rutaRecoleccionRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        // Evitar cambios si la ruta ya está COMPLETADA o CANCELADA
        if (ruta.getEstado() == EstadoRuta.FINALIZADA || ruta.getEstado() == EstadoRuta.CANCELADA) {
            throw new IllegalStateException("No se puede cambiar el estado de una ruta finalizada o cancelada");
        }

        ruta.setEstado(nuevoEstado);
        rutaRecoleccionRepository.save(ruta);
        return convertirADTO(ruta);
    }

    // ========================================================
    // ASIGNAR RECOLECCIONES A LA RUTA
    // ========================================================
    @Override
    @Transactional
    public RutaRecoleccionDTO asignarRecolecciones(Long rutaId, List<Long> recoleccionesIds) {
        RutaRecoleccionEntity ruta = rutaRecoleccionRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        List<RecoleccionEntity> recolecciones = recoleccionRepository.findAllById(recoleccionesIds);
        ruta.setRecolecciones(recolecciones);

        rutaRecoleccionRepository.save(ruta);
        return convertirADTO(ruta);
    }

    // ========================================================
    // ELIMINAR RUTA
    // ========================================================
    @Override
    @Transactional
    public void eliminarRuta(Long rutaId) {
        RutaRecoleccionEntity ruta = rutaRecoleccionRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        if (ruta.getEstado() != EstadoRuta.PLANIFICADA) {
            throw new IllegalStateException("Solo se pueden eliminar rutas PLANIFICADAS");
        }

        rutaRecoleccionRepository.delete(ruta);
    }

    // ========================================================
    // CONVERTIR ENTITY A DTO
    // ========================================================
    private RutaRecoleccionDTO convertirADTO(RutaRecoleccionEntity ruta) {
        RutaRecoleccionDTO dto = new RutaRecoleccionDTO();
        dto.setIdRuta(ruta.getIdRuta());
        dto.setEstado(ruta.getEstado());
        dto.setRecolectorId(ruta.getRecolector().getIdUsuario());
        dto.setFechaCreacion(ruta.getFechaCreacion());

        if (ruta.getRecolecciones() != null) {
    dto.setRecoleccionesIds(
        ruta.getRecolecciones()
             .stream()
             .map(RecoleccionEntity::getIdRecoleccion)
             .map(Long::valueOf) // <--- asegura que sea Long
             .collect(Collectors.toList())
    );
}


        return dto;
    }
}

package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.OSRMService;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutaRecoleccionServiceImpl implements RutaRecoleccionService {

    private final RutaRecoleccionRepository rutaRepository;
    private final RecoleccionRepository recoleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final OSRMService osrmService;

    @Override
    @Transactional
    public RutaRecoleccionDTO crearRuta(RutaRecoleccionDTO dto, Long recolectorId) {

        UsuarioEntity recolector = usuarioRepository.findById(recolectorId)
                .orElseThrow(() -> new EntityNotFoundException("Recolector no encontrado"));

        RutaRecoleccionEntity ruta = new RutaRecoleccionEntity();
        ruta.setNombre(dto.getNombre());
        ruta.setEstado(EstadoRuta.PLANIFICADA);
        ruta.setRecolector(recolector);

        if (dto.getRecoleccionIds() != null) {

            List<RecoleccionEntity> recolecciones =
                    recoleccionRepository.findAllById(dto.getRecoleccionIds());

            if (recolecciones.size() < 2) {
                throw new IllegalStateException("Se necesitan al menos 2 puntos");
            }

            for (RecoleccionEntity r : recolecciones) {

                if (r.getEstado() != EstadoRecoleccion.Pendiente) {
                    throw new IllegalStateException("Solo pendientes");
                }

                if (r.getRuta() != null) {
                    throw new IllegalStateException("Ya pertenece a ruta");
                }

                r.setRuta(ruta);
            }

            List<double[]> coords = recolecciones.stream()
                    .map(r -> new double[]{
                            r.getSolicitud().getLongitude().doubleValue(),
                            r.getSolicitud().getLatitude().doubleValue()
                    })
                    .toList();

            var resultado = osrmService.calcularRuta(coords);

            ruta.setDistanciaTotal(resultado.getDistancia());
            ruta.setTiempoEstimado(resultado.getDuracion());
            ruta.setGeometriaRuta(resultado.getGeometria());

            ruta.setRecolecciones(recolecciones);
            recoleccionRepository.saveAll(recolecciones);
        }

        return convertirDTO(rutaRepository.save(ruta));
    }

    @Override
    public RutaRecoleccionDTO obtenerPorId(Long id) {
        return rutaRepository.findById(id)
                .map(this::convertirDTO)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
    }

    @Override
    public List<RutaRecoleccionDTO> listarTodas() {
        return rutaRepository.findAll().stream().map(this::convertirDTO).toList();
    }

    @Override
    public List<RutaRecoleccionDTO> listarPorEstado(EstadoRuta estado) {
        return rutaRepository.findByEstado(estado).stream().map(this::convertirDTO).toList();
    }

    @Override
    public List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId) {
        return rutaRepository.findByRecolector_IdUsuario(recolectorId).stream().map(this::convertirDTO).toList();
    }

    @Override
    @Transactional
    public RutaRecoleccionDTO iniciarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        if (ruta.getEstado() != EstadoRuta.PLANIFICADA) {
            throw new IllegalStateException("Solo rutas planificadas pueden iniciar");
        }

        ruta.setEstado(EstadoRuta.EN_PROGRESO);
        ruta.getRecolecciones().forEach(r -> r.setEstado(EstadoRecoleccion.En_Progreso));

        return convertirDTO(rutaRepository.save(ruta));
    }

    @Override
    @Transactional
    public RutaRecoleccionDTO finalizarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        if (ruta.getEstado() != EstadoRuta.EN_PROGRESO) {
            throw new IllegalStateException("Ruta no está en progreso");
        }

        ruta.setEstado(EstadoRuta.FINALIZADA);
        ruta.getRecolecciones().forEach(r -> r.setEstado(EstadoRecoleccion.Completada));

        return convertirDTO(rutaRepository.save(ruta));
    }

    private RutaRecoleccionDTO convertirDTO(RutaRecoleccionEntity r) {
        RutaRecoleccionDTO dto = new RutaRecoleccionDTO();
        dto.setIdRuta(r.getIdRuta());
        dto.setNombre(r.getNombre());
        dto.setEstado(r.getEstado());
        dto.setDistanciaTotal(r.getDistanciaTotal());
        dto.setTiempoEstimado(r.getTiempoEstimado());
        dto.setGeometriaRuta(r.getGeometriaRuta());
dto.setFechaCreacion(r.getFechaCreacion().atOffset(ZoneOffset.systemDefault().getRules().getOffset(r.getFechaCreacion())));
        dto.setRecolectorId(r.getRecolector() != null ? r.getRecolector().getIdUsuario() : null);

        if (r.getRecolecciones() != null) {
            dto.setRecoleccionIds(r.getRecolecciones().stream()
                    .map(RecoleccionEntity::getIdRecoleccion)
                    .toList());
        }

        return dto;
    }
}
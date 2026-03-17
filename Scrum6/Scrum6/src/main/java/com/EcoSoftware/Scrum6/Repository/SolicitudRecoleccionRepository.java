package com.EcoSoftware.Scrum6.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;

@Repository
public interface SolicitudRecoleccionRepository extends JpaRepository<SolicitudRecoleccionEntity, Long> {


    // Buscar solicitudes por estado
    List<SolicitudRecoleccionEntity> findByEstadoPeticion(EstadoPeticion estadoPeticion);

    // Buscar solicitudes de un usuario (ciudadano)
    List<SolicitudRecoleccionEntity> findByUsuario_IdUsuario(Long usuarioId);

    // Buscar solicitudes aceptadas por un recolector/empresa
    List<SolicitudRecoleccionEntity> findByAceptadaPor_IdUsuario(Long usuarioId);

    // Buscar solicitudes por localidad
    List<SolicitudRecoleccionEntity> findByLocalidad(Localidad localidad);

    // Buscar solicitudes por tipo de residuo
    List<SolicitudRecoleccionEntity> findByTipoResiduo(TipoResiduo tipoResiduo);

    // Buscar solicitudes de un usuario en un estado específico
    List<SolicitudRecoleccionEntity> findByUsuario_IdUsuarioAndEstadoPeticion(Long usuarioId, EstadoPeticion estadoPeticion);

    // Buscar solicitudes por localidad y tipo de residuo
    List<SolicitudRecoleccionEntity> findByLocalidadAndTipoResiduo(Localidad localidad, TipoResiduo tipoResiduo);
    List<SolicitudRecoleccionEntity> findByLocalidadAndEstadoPeticion(Localidad localidad, EstadoPeticion estadoPeticion);

    // crear graficos 

    // Consulta agrupada por motivo de rechazo
@Query("SELECT COALESCE(s.motivoRechazo, 'Sin motivo registrado'), COUNT(s) " +
       "FROM SolicitudRecoleccionEntity s " +
       "WHERE s.estadoPeticion = com.EcoSoftware.Scrum6.Enums.EstadoPeticion.Rechazada " +
       "GROUP BY s.motivoRechazo")
List<Object[]> obtenerRechazadasAgrupadasPorMotivo();

// Contar solicitudes aceptadas
@Query("SELECT COUNT(s) FROM SolicitudRecoleccionEntity s WHERE s.estadoPeticion = com.EcoSoftware.Scrum6.Enums.EstadoPeticion.Aceptada")
Long countAceptadas();

// Contar solicitudes pendientes
@Query("SELECT COUNT(s) FROM SolicitudRecoleccionEntity s WHERE s.estadoPeticion = com.EcoSoftware.Scrum6.Enums.EstadoPeticion.Pendiente")
Long countPendientes();

@Query("SELECT COUNT(s) FROM SolicitudRecoleccionEntity s WHERE s.estadoPeticion = com.EcoSoftware.Scrum6.Enums.EstadoPeticion.Rechazada")
Long countRechazadas();

// Solicitudes por localidad
@Query("SELECT CAST(s.localidad AS string), COUNT(s) FROM SolicitudRecoleccionEntity s GROUP BY s.localidad")
List<Object[]> obtenerSolicitudesPorLocalidad();



    @Query("SELECT s.usuario.idUsuario FROM SolicitudRecoleccionEntity s WHERE s.idSolicitud = :idSolicitud")
    Optional<Long> findUsuarioIdByIdSolicitud(@Param("idSolicitud") Long idSolicitud);
}

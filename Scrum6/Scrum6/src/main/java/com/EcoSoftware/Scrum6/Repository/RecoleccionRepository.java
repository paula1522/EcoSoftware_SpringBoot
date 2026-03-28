package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecoleccionRepository extends JpaRepository<RecoleccionEntity, Long> {

    // Todas las recolecciones que no estén canceladas
    List<RecoleccionEntity> findByEstadoNot(EstadoRecoleccion estado);

    // Recolecciones activas de un recolector
    List<RecoleccionEntity> findByRecolector_IdUsuarioAndEstadoNot(Long recolectorId, EstadoRecoleccion estado);
List<RecoleccionEntity> findByRecolector_IdUsuarioAndEstado(Long recolectorId, EstadoRecoleccion estado);
    // Buscar recolecciones por el ID del usuario que creó la solicitud
List<RecoleccionEntity> findBySolicitud_Usuario_IdUsuario(Long usuarioId);
    List<RecoleccionEntity> findByRecolector_IdUsuario(Long recolectorId);
    // Recolecciones activas de una ruta
    List<RecoleccionEntity> findByRuta_IdRutaAndEstadoNot(Long rutaId, EstadoRecoleccion estado);

    // Recolecciones por estado específico
    List<RecoleccionEntity> findByEstado(EstadoRecoleccion estado);

     // Listar recolecciones de un recolector que aún no están asignadas a una ruta
    List<RecoleccionEntity> findByRecolector_IdUsuarioAndRutaIsNullAndEstadoNot(
            Long recolectorId,
            EstadoRecoleccion estado
    );
  // Método con EntityGraph para cargar las relaciones necesarias
    @EntityGraph(attributePaths = {"solicitud", "ruta", "recolector"})
    @Query("SELECT r FROM RecoleccionEntity r WHERE r.idRecoleccion = :id")
    Optional<RecoleccionEntity> findByIdWithRelations(@Param("id") Long id);


    // === DASHBOARDS OPTIMIZADOS ===

    // Reciclador Dashboard - Recolecciones por mes
    @Query("SELECT YEAR(r.fechaCreacionRecoleccion) as anio, " +
           "MONTH(r.fechaCreacionRecoleccion) as mes, " +
           "COUNT(r) as cantidad " +
           "FROM RecoleccionEntity r " +
           "WHERE r.recolector.idUsuario = :recolectorId AND r.fechaCreacionRecoleccion IS NOT NULL " +
           "GROUP BY YEAR(r.fechaCreacionRecoleccion), MONTH(r.fechaCreacionRecoleccion) " +
           "ORDER BY YEAR(r.fechaCreacionRecoleccion), MONTH(r.fechaCreacionRecoleccion)")
    List<Object[]> obtenerMesesReciclador(@Param("recolectorId") Long recolectorId);

    // Reciclador Dashboard - Recolecciones por estado
    @Query("SELECT r.estado, COUNT(r) as cantidad " +
           "FROM RecoleccionEntity r " +
           "WHERE r.recolector.idUsuario = :recolectorId " +
           "GROUP BY r.estado " +
           "ORDER BY r.estado")
    List<Object[]> obtenerEstadosReciclador(@Param("recolectorId") Long recolectorId);

    // Reciclador Dashboard - Conteo de recolecciones completadas
    @Query("SELECT COUNT(r) FROM RecoleccionEntity r " +
           "WHERE r.recolector.idUsuario = :recolectorId")
    Long obtenerTotalRecolecciones(@Param("recolectorId") Long recolectorId);

    @Query("SELECT COUNT(r) FROM RecoleccionEntity r " +
           "WHERE r.recolector.idUsuario = :recolectorId AND r.estado = com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.Completada")
    Long obtenerRecoleccionesCompletadas(@Param("recolectorId") Long recolectorId);

    // Reciclador Dashboard - Material recolectado por tipo (excluye Cancelada y Fallida)
    @Query("SELECT r.solicitud.tipoResiduo, r.solicitud.cantidad " +
           "FROM RecoleccionEntity r " +
           "WHERE r.recolector.idUsuario = :recolectorId " +
           "AND r.estado NOT IN (com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.Cancelada, com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.Fallida)")
    List<Object[]> obtenerMaterialRecolectadoReciclador(@Param("recolectorId") Long recolectorId);
}



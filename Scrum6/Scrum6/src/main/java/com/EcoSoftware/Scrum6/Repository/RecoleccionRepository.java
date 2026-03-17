package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecoleccionRepository extends JpaRepository<RecoleccionEntity, Long> {

    // Todas las recolecciones que no estén canceladas
    List<RecoleccionEntity> findByEstadoNot(EstadoRecoleccion estado);

    // Recolecciones activas de un recolector
    List<RecoleccionEntity> findByRecolector_IdUsuarioAndEstadoNot(Long recolectorId, EstadoRecoleccion estado);

    //
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
}




package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRecoleccionRepository extends JpaRepository<RutaRecoleccionEntity, Long> {

    // Rutas por estado
    List<RutaRecoleccionEntity> findByEstado(EstadoRuta estado);

    // Rutas de un recolector específico
    List<RutaRecoleccionEntity> findByRecolector_IdUsuario(Long recolectorId);

    // Rutas activas de un recolector
    List<RutaRecoleccionEntity> findByRecolector_IdUsuarioAndEstado(
            Long recolectorId,
            EstadoRuta estado
    );
}

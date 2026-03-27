package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRecoleccionRepository extends JpaRepository<RutaRecoleccionEntity, Long> {

    List<RutaRecoleccionEntity> findByEstado(EstadoRuta estado);

    // Cargar eager las recolecciones y sus solicitudes para evitar LazyInitializationException
    @EntityGraph(attributePaths = {"recolecciones", "recolecciones.solicitud"})
    List<RutaRecoleccionEntity> findByRecolector_IdUsuario(Long recolectorId);
}
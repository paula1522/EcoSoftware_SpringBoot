package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRecoleccionRepository extends JpaRepository<RutaRecoleccionEntity, Long> {

    List<RutaRecoleccionEntity> findByEstado(EstadoRuta estado);

    // Cargar eager las recolecciones y sus solicitudes para evitar LazyInitializationException
    @EntityGraph(attributePaths = {"recolecciones", "recolecciones.solicitud"})
    List<RutaRecoleccionEntity> findByRecolector_IdUsuario(Long recolectorId);

 // Método con EntityGraph para cargar las relaciones necesarias
   @EntityGraph(attributePaths = {"recolector", "recolecciones", "recolecciones.solicitud"})
    @Query("SELECT r FROM RutaRecoleccionEntity r WHERE r.idRuta = :id")
    Optional<RutaRecoleccionEntity> findByIdWithRelations(@Param("id") Long id);
}
package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRecoleccionRepository extends JpaRepository<RutaRecoleccionEntity, Long> {

    List<RutaRecoleccionEntity> findByEstado(EstadoRuta estado);

    List<RutaRecoleccionEntity> findByRecolector_IdUsuario(Long recolector);

    List<RutaRecoleccionEntity> findByRecolector_IdUsuarioAndEstado(
            Long recolectorId,
            EstadoRuta estado
    );
}
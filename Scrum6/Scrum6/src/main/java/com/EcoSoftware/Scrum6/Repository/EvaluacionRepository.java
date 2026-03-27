package com.EcoSoftware.Scrum6.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.EvaluacionEntity;

@Repository
public interface EvaluacionRepository extends JpaRepository<EvaluacionEntity, Long> {

    List<EvaluacionEntity> findByModuloId(Long moduloId);

    List<EvaluacionEntity> findByModulo_Capacitacion_Id(Long cursoId);

    long countByModulo_Capacitacion_Id(Long cursoId);

    void deleteByModuloId(Long moduloId);
}

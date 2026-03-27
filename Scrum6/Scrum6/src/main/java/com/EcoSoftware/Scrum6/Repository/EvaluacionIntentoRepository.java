package com.EcoSoftware.Scrum6.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.EvaluacionIntentoEntity;

@Repository
public interface EvaluacionIntentoRepository extends JpaRepository<EvaluacionIntentoEntity, Long> {

    List<EvaluacionIntentoEntity> findByEvaluacionIdAndUsuario_IdUsuario(Long evaluacionId, Long usuarioId);

    long countDistinctByEvaluacion_IdAndUsuario_IdUsuarioAndAprobadoTrue(Long evaluacionId, Long usuarioId);

    long countDistinctByEvaluacion_Modulo_Capacitacion_IdAndUsuario_IdUsuarioAndAprobadoTrue(Long cursoId, Long usuarioId);

    void deleteByEvaluacionId(Long evaluacionId);
}

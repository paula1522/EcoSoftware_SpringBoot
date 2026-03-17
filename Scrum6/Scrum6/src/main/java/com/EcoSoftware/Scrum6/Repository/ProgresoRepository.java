package com.EcoSoftware.Scrum6.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.ProgresoEntity;

@Repository
public interface ProgresoRepository extends JpaRepository<ProgresoEntity, Long> {

    List<ProgresoEntity> findByUsuario_IdUsuario(Long usuarioId);

    List<ProgresoEntity> findByCursoId(Long cursoId);
}

package com.EcoSoftware.Scrum6.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.InscripcionEntity;

@Repository
public interface InscripcionRepository extends JpaRepository<InscripcionEntity, Long> {

    List<InscripcionEntity> findByUsuario_IdUsuario(Long usuarioId);

    List<InscripcionEntity> findByCursoId(Long cursoId);
}

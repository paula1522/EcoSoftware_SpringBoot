package com.EcoSoftware.Scrum6.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.ModuloEntity;

@Repository
public interface ModuloRepository extends JpaRepository<ModuloEntity, Long> {

    List<ModuloEntity> findByCapacitacionId(Long capacitacionId);
}

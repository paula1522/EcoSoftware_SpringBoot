package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RolEntity;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<RolEntity, Long > {
    // Buscar rol por nombre
    Optional<RolEntity> findByNombreIgnoreCase(String nombre);
    Optional<RolEntity> findByTipo(RolEntity.TipoDeRol tipo);

}

package com.EcoSoftware.Scrum6.Repository;

import java.util.List;
import java.util.Optional;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.EcoSoftware.Scrum6.Entity.CapacitacionEntity;

@Repository
public interface CapacitacionRepository extends JpaRepository<CapacitacionEntity, Long> {

    List<CapacitacionEntity> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByDescripcionIgnoreCase(String descripcion);

    Optional<CapacitacionesDTO> findByNombre(String nombre);


}

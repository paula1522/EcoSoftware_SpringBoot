package com.EcoSoftware.Scrum6.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.PuntoReciclaje;

@Repository
public interface PuntoRepository extends JpaRepository<PuntoReciclaje, Long> {

	Optional<PuntoReciclaje> findByNombreIgnoreCaseAndDireccionIgnoreCase(String nombre, String direccion);

	boolean existsByNombreIgnoreCaseAndDireccionIgnoreCase(String nombre, String direccion);

	boolean existsByNombreIgnoreCaseAndDireccionIgnoreCaseAndIdNot(String nombre, String direccion, Long id);
}

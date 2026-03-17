package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {


    void findByRol(RolEntity rol);
    
    Optional<UsuarioEntity> findByNitAndEstadoTrue(String nit);
    Optional<UsuarioEntity> findByCedulaAndEstadoTrue(String cedula);
    List<UsuarioEntity> findByNitContainingIgnoreCaseAndEstadoTrue(String nit);
    List<UsuarioEntity> findByCedulaContainingIgnoreCaseAndEstadoTrue(String cedula);
    Optional<UsuarioEntity> findByNombreAndEstadoTrue(String nombre);
    List<UsuarioEntity> findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre);
    List<UsuarioEntity> findAllByOrderByIdUsuarioAsc();
    Optional<UsuarioEntity> findByCorreoAndEstadoTrue(String correo);

    List<UsuarioEntity> findByCorreoContainingIgnoreCaseAndEstadoTrue(String correo);

    Optional<UsuarioEntity> findByCorreo(String correo);

List<UsuarioEntity> findByEstadoRegistro(EstadoRegistro estadoRegistro);
Long countByEstadoRegistro(EstadoRegistro estadoRegistro);


    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.estado = false WHERE u.idUsuario = :id")
    int eliminacionLogica(@Param("id") Long id);

    @Query("SELECT u FROM UsuarioEntity u " +
            "WHERE u.estado = true " +
            "AND (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:correo IS NULL OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%'))) " +
            "AND (:documento IS NULL OR LOWER(u.cedula) LIKE LOWER(CONCAT('%', :documento, '%')) " +
            "OR LOWER(u.nit) LIKE LOWER(CONCAT('%', :documento, '%')))")
    List<UsuarioEntity> findByFiltros(@Param("nombre") String nombre,
                                      @Param("correo") String correo,
                                      @Param("documento") String documento);

    List<UsuarioEntity> findByEstadoTrue();
    @Query("SELECT u.localidad AS localidad, COUNT(u) AS total " +
            "FROM UsuarioEntity u " +
            "WHERE u.estado = true " +
            "GROUP BY u.localidad")
    List<Object[]> contarUsuariosPorLocalidad();
    Optional<UsuarioEntity> findByCorreoIgnoreCase(String correo);
}

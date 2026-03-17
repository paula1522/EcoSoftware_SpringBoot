package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RolDTO;

import java.util.List;

public interface RolService {
    List<RolDTO> listarRoles();

    RolDTO crearRol(RolDTO rolDTO);

    RolDTO actualizarRol(Long idRol, RolDTO rolDTO);

    void eliminarRol(Long idRol);
}

package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RolDTO;
import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Service.RolService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolImpl implements RolService {
    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RolDTO> listarRoles(){
        return rolRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    @Override
    public RolDTO crearRol(RolDTO rolDTO){
        RolEntity rolEntity = modelMapper.map(rolDTO, RolEntity.class);
                rolEntity = rolRepository.save(rolEntity);
        return modelMapper.map(rolEntity, RolDTO.class);
    }

    @Override
    public RolDTO actualizarRol(Long idRol, RolDTO rolDTO){
        RolEntity rolExistente = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol con ID: " + idRol));
        rolExistente.setNombre(rolDTO.getNombre());
        rolExistente.setDescripcion(rolDTO.getDescripcion());
        rolExistente = rolRepository.save(rolExistente);
        return modelMapper.map(rolExistente, RolDTO.class);



        
    }


    public void eliminarRol(Long idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new RuntimeException("Rol no encontrado con ID: " + idRol);
        }
        rolRepository.deleteById(idRol);
    }


    public RolDTO convertirADTO(RolEntity rolEntity) {
        return modelMapper.map(rolEntity, RolDTO.class);
    }

}

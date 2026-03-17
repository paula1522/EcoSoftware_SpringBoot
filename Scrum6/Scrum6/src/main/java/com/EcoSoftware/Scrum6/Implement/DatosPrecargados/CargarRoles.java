package com.EcoSoftware.Scrum6.Implement.DatosPrecargados;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargarRoles implements CommandLineRunner {

    private final RolRepository rolRepository;

    public CargarRoles(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        crearRolSiNoExiste("Administrador", "Administrador del sistema",
                RolEntity.TipoDeRol.Administrador);
        crearRolSiNoExiste("Ciudadano",
                "Usuario ciudadano (Hace solicitudes, se inscribe a capacitaciones y consulta punto)", RolEntity.TipoDeRol.Ciudadano);
        crearRolSiNoExiste("Empresa", "Usuario empresa (Gestiona puntos limpios, recolecciones y rutas de recolección)",
                RolEntity.TipoDeRol.Empresa);
        crearRolSiNoExiste("Reciclador",
                "Usuario reciclador (Gestiona materiales reciclables y realiza recolecciones)",
                RolEntity.TipoDeRol.Reciclador);

        System.out.println(" Roles verificados/creados correctamente");
    }

    private void crearRolSiNoExiste(String nombre, String descripcion, RolEntity.TipoDeRol tipo) {
        rolRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> {
                    RolEntity r = new RolEntity();
                    r.setNombre(nombre);
                    r.setDescripcion(descripcion);
                    r.setTipo(tipo);
                    return rolRepository.save(r);
                });
    }
}

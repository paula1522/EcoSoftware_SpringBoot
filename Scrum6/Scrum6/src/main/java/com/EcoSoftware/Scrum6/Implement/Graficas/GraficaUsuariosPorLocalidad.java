package com.EcoSoftware.Scrum6.Implement.Graficas;

import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.graficasDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("graficaUsuariosLocalidad")

public class GraficaUsuariosPorLocalidad implements graficasDatos {
    @Autowired

    private UsuarioRepository usuarioRepository;

    @Override
    public Map<String, Object> traerDatos() {
        Map<String, Object> datos = new HashMap<>();

        List<Object[]> lista = usuarioRepository.contarUsuariosPorLocalidad();

        for (Object[] objeto : lista) {

            String localidad = (String) objeto[0];
            Long cantidad = (Long) objeto[1];

            // Si la localidad es null, asignar un nombre válido
            if (localidad == null || localidad.trim().isEmpty()) {
                localidad = "Localidad desconocida";
            }

            datos.put(localidad, cantidad);
        }

        return datos;
    }

}

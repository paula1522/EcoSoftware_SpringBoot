package com.EcoSoftware.Scrum6.Implement.Graficas;

import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.graficasDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("graficaEstados")
public class GraficaSolicitudesAceptadasRechazadas implements graficasDatos {

    @Autowired
    private SolicitudRecoleccionRepository solicitudRepository;

    @Override
    public Map<String, Object> traerDatos() {

        Map<String, Object> datos = new HashMap<>();

        // Conteos simples usando métodos del repositorio
        Long aceptadas = solicitudRepository.countAceptadas();
        Long pendientes = solicitudRepository.countPendientes();
        Long rechazadas = solicitudRepository.countRechazadas();

        // Agregar al JSON de salida
        datos.put("aceptadas", aceptadas);
        datos.put("pendientes", pendientes);
        datos.put("rechazadas", rechazadas);

        return datos;
    }
}

package com.EcoSoftware.Scrum6.Implement.Graficas;

import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.graficasDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("graficaLocalidades")
public class GraficaSolicitudesPorLocalidad implements graficasDatos {

    @Autowired
    private SolicitudRecoleccionRepository solicitudRepository;

    @Override
    public Map<String, Object> traerDatos() {
        Map<String, Object> datos = new HashMap<>();

        List<Object[]> lista = solicitudRepository.obtenerSolicitudesPorLocalidad();

        for (Object[] row : lista) {
            String localidad = row[0].toString();
            Long cantidad = Long.valueOf(row[1].toString());
            datos.put(localidad, cantidad);
        }

        return datos;
    }
}

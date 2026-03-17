package com.EcoSoftware.Scrum6.Implement.Factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.EcoSoftware.Scrum6.Service.graficasDatos;

@Component
public class GraficasFactory {

    @Autowired
    private ApplicationContext context;

    public graficasDatos obtenerGrafica(String tipo) {

        switch (tipo.toLowerCase()) {

            case "solicitudes-estado":
                return context.getBean("graficaEstados", graficasDatos.class);
            case "solicitudes-localidad":
                return context.getBean("graficaLocalidades", graficasDatos.class);
            case "usuarios-localidad":
                return context.getBean("graficaUsuariosLocalidad", graficasDatos.class);
            default:
                throw new IllegalArgumentException("Tipo de gráfica no soportado: " + tipo);
        }
    }
}

package com.EcoSoftware.Scrum6.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.Implement.Factory.GraficasFactory;
import com.EcoSoftware.Scrum6.Service.graficasDatos;

@RestController
@RequestMapping("/api/graficos")
@CrossOrigin(origins = "*")
public class GraficasController {

    @Autowired
    private GraficasFactory graficasFactory;

    /**
     * Endpoint genérico para obtener cualquier gráfica
     * Ejemplos:
     * - /api/graficos/solicitudes-estado
     * - /api/graficos/solicitudes-localidad
     * - /api/graficos/usuarios-localidad
     */
    @GetMapping("/{tipo}")
    public ResponseEntity<?> obtenerGraficaPorFactory(@PathVariable String tipo) {
        graficasDatos grafica = graficasFactory.obtenerGrafica(tipo);
        return ResponseEntity.ok(grafica.traerDatos());
    }
}

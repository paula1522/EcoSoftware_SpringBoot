package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RutaRecoleccionDTO {
    private Long idRuta;
    private String nombre;
    private EstadoRuta estado;
    private Long recolectorId;
    private List<Long> recoleccionIds;      // ids en orden
    private List<ParadaDTO> paradas;        // lista de paradas ordenadas
    private Double distanciaTotal;
    private Double tiempoEstimado;
    private String geometriaRuta;           // polyline en JSON
    private LocalDateTime fechaCreacion;
}
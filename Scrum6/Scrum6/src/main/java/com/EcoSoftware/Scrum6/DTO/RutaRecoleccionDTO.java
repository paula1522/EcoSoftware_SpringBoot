package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class RutaRecoleccionDTO {

    private Long idRuta;
    private String nombre;
    private EstadoRuta estado;

    private List<Long> recoleccionIds;
    private Long recolectorId;
    private Double distanciaTotal;
    private Double tiempoEstimado;
    private String geometriaRuta;

    private OffsetDateTime fechaCreacion;
}
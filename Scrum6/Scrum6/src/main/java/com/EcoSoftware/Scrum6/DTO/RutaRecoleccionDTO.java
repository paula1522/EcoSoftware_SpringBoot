package com.EcoSoftware.Scrum6.DTO;


import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class RutaRecoleccionDTO {

    private Long idRuta;
    private Long recolectorId;

    private EstadoRuta estado;
    private OffsetDateTime fechaCreacion;

    private List<Long> recoleccionesIds; // solo IDs, no objetos completos
}


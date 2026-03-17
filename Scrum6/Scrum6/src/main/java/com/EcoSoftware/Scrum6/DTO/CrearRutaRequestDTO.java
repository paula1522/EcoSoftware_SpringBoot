package com.EcoSoftware.Scrum6.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CrearRutaRequestDTO {

    private Long recolectorId;
    private Double latActual;
    private Double lngActual;
    private List<Long> recoleccionesIds;
}

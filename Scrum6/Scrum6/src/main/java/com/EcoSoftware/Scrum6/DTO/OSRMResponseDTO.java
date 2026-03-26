package com.EcoSoftware.Scrum6.DTO;

import lombok.Data;

@Data
public class OSRMResponseDTO {

    private Double distancia; // km
    private Double duracion;  // minutos
    private String geometria; // polyline

}
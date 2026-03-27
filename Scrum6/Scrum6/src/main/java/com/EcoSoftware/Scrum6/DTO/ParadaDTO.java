package com.EcoSoftware.Scrum6.DTO;

import java.math.BigDecimal;

import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParadaDTO {
    private Long recoleccionId;
    private Integer ordenParada;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private EstadoRecoleccion estado;
}
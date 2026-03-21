package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecicladorDashboardCumplimientoDTO {
    private String metrica;           // ej: "Cumplimiento total", "Completadas en tiempo"
    private Double porcentaje;        // 0-100
    private Long completadas;
    private Long total;
}

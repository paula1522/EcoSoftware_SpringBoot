package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecicladorDashboardPeriodoDTO {
    private String periodo;  // semana o mes
    private Long cantidad;   // número de recolecciones
}

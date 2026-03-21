package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CiudadanoDashboardImpactoDTO {
    private String metrica;       // ej: "Kg reciclados", "CO2 evitado", "Árboles salvados"
    private Double valor;         // ej: 150.5, 250.8, 3
    private String unidad;        // ej: "kg", "kg CO2", "árboles"
}

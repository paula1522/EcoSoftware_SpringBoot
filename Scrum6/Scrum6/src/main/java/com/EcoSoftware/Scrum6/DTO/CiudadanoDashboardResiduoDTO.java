package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CiudadanoDashboardResiduoDTO {
    private String tipoResiduo;
    private Double cantidad;
}

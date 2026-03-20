package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDashboardTiempoPromedioDTO {
    private String mes;
    private Double promedioHoras;
}
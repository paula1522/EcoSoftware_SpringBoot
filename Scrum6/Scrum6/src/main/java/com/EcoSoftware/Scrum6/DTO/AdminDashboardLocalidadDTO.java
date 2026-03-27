package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardLocalidadDTO {
    private String localidad;
    private Long cantidad;
}

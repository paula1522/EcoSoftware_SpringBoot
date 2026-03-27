package com.EcoSoftware.Scrum6.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardEstadoDTO {
    private String estado;
    private Long cantidad;
}

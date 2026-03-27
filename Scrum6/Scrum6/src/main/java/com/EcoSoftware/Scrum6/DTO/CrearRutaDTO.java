package com.EcoSoftware.Scrum6.DTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CrearRutaDTO {
    @NotBlank
    private String nombre;

    @NotEmpty
    private List<Long> recoleccionIds; // IDs de recolección en el orden deseado
}
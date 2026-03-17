package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RolDTO {
    private Long idRol;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener mínimo 2 y máximo 50 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 500, message = "La descripción debe tener mínimo 5 y máximo 500 caracteres")
    private String descripcion;

    private RolEntity.TipoDeRol tipo;

    private boolean estado;
}

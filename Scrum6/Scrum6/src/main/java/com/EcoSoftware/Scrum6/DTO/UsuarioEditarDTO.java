package com.EcoSoftware.Scrum6.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEditarDTO {

    @Size(min = 2, max = 70, message = "El nombre debe tener entre 2 y 70 caracteres")
    private String nombre;

    private String contrasena;

    @Email(message = "El correo debe ser válido")
    private String correo;

    private String telefono;

    private String direccion;

    private String barrio;

    private String localidad;

    private String zona_de_trabajo;

    private String horario;

    private String certificaciones;

    private String imagen_perfil;

    private Integer cantidad_minima;

    private Boolean estado;

}

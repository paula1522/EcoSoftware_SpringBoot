package com.EcoSoftware.Scrum6.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    //Todos los usuarios
    private Long idUsuario;

    @NotNull(message = "El rol es obligatorio")
    private Long rolId;

    @NotBlank
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
        message = "La contraseña debe tener mínimo 8 caracteres e incluir mayúscula, minúscula, número y un carácter especial"
    )
    private String contrasena;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String cedula;

    @NotBlank
    private String telefono;

    private String direccion;


    private String barrio;


    private String localidad;

    //empresa
    private String nit;

    private String representanteLegal;

    private String Rut;


    private String zona_de_trabajo;

    private String horario;

    private String tipoMaterial;

    private Integer cantidad_minima;

    private String imagen_perfil;

    private String certificaciones;

    private String Documento;

    private String camaraComercio;

    private Boolean estado;

    private LocalDateTime fechaCreacion;

    private EstadoRegistro estadoRegistro;
}


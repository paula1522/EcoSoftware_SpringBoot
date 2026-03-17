package com.EcoSoftware.Scrum6.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.List;

@Entity
@Table(name= "roles")
@Data


public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long idRol;

    @OneToMany(mappedBy = "rol")
    private List<UsuarioEntity> usuarios;

    @Column(nullable = false)
    @Size(min = 2, max = 50, message = "El nombre debe tener minimo 2 y maximo 50 caracteres")
    private String nombre;

    @Column(nullable = false)
    @Size(min = 2, max = 500, message = "La descripcion debe tener minimo 5 y maximo 500 caracteres")
    private String descripcion;

    @Column
    @Enumerated(EnumType.STRING)
    private TipoDeRol tipo;

    @Column
    private boolean estado = true;

    public enum TipoDeRol {
        Administrador,
        Ciudadano,
        Empresa,
        Reciclador
    }
}

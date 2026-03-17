package com.EcoSoftware.Scrum6.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "capacitacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacitacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String numeroDeClases;
    private String duracion;
    private String imagen;

    @OneToMany(mappedBy = "capacitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuloEntity> modulos;
}

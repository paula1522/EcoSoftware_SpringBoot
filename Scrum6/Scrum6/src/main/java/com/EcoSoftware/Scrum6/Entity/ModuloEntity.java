package com.EcoSoftware.Scrum6.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modulo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String duracion;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "capacitacion_id")
    private CapacitacionEntity capacitacion;
}

package com.EcoSoftware.Scrum6.Entity;

import java.time.LocalDate;

import com.EcoSoftware.Scrum6.Enums.EstadoCurso;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "inscripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaDeInscripcion;

    @Enumerated(EnumType.STRING)
    private EstadoCurso estadoCurso;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CapacitacionEntity curso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
}

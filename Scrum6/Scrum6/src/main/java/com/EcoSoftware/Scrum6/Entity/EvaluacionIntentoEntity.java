package com.EcoSoftware.Scrum6.Entity;

import java.time.LocalDate;

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
@Table(name = "evaluacion_intento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionIntentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double puntajeObtenido;
    private Boolean aprobado;
    private LocalDate fechaPresentacion;

    @ManyToOne
    @JoinColumn(name = "evaluacion_id")
    private EvaluacionEntity evaluacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
}

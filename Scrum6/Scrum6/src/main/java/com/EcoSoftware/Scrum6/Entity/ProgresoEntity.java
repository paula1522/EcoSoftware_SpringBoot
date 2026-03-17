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
@Table(name = "progreso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String progresoDelCurso;
    private String modulosCompletados;
    private String tiempoInvertido;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CapacitacionEntity curso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
}

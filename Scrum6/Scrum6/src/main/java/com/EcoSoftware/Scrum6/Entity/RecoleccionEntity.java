package com.EcoSoftware.Scrum6.Entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recoleccion")
public class RecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecoleccion;

    // Solicitud que dio origen a esta recolección
    @OneToOne
    @JoinColumn(name = "solicitud_id", nullable = false, unique = true)
    private SolicitudRecoleccionEntity solicitud;

    // Usuario (reciclador o empresa) que aceptó la solicitud
    @ManyToOne
    @JoinColumn(name = "recolector_id", nullable = false)
    private UsuarioEntity recolector;

    // Ruta a la que pertenece la recolección (puede ser null al inicio)
    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = true)
    private RutaRecoleccionEntity ruta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
private EstadoRecoleccion estado = EstadoRecoleccion.Pendiente;

    @Column(name = "fecha_recoleccion")
    private LocalDateTime fechaRecoleccion;

    private Integer ordenParada;


    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(length = 500)
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_creacion_recoleccion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacionRecoleccion;

  
}


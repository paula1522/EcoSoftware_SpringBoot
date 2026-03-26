package com.EcoSoftware.Scrum6.Entity;

import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ruta_recoleccion")
@Getter
@Setter
public class RutaRecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuta;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private EstadoRuta estado;

    private Double distanciaTotal;
    private Double tiempoEstimado;

    @Column(length = 2000)
    private String geometriaRuta; // polyline u otro formato

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // =====================================
    // RELACIÓN CON RECOLECTOR
    // =====================================
    @ManyToOne
    @JoinColumn(name = "recolector_id", nullable = false)
    private UsuarioEntity recolector;

    // =====================================
    // RELACIÓN CON RECOLECCIONES
    // =====================================
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
    private List<RecoleccionEntity> recolecciones;
}
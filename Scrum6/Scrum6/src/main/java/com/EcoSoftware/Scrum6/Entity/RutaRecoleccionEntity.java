package com.EcoSoftware.Scrum6.Entity;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.EcoSoftware.Scrum6.Enums.EstadoRuta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ruta_recoleccion")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RutaRecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuta;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "recolector_id", nullable = false)
private UsuarioEntity recolector;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRuta estado = EstadoRuta.PLANIFICADA;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @OneToMany(mappedBy = "ruta", fetch = FetchType.LAZY)
private List<RecoleccionEntity> recolecciones;

}

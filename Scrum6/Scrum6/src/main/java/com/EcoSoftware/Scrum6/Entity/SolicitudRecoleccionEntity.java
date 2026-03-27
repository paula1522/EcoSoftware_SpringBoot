package com.EcoSoftware.Scrum6.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "solicitud_recoleccion")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudRecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aceptada_por_id")
    private UsuarioEntity aceptadaPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false, length = 20)
    @NotNull
    private TipoResiduo tipoResiduo;

    @Column(nullable = false, length = 100)
    @NotNull
    private String cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_peticion", nullable = false)
    private EstadoPeticion estadoPeticion = EstadoPeticion.Pendiente;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Localidad localidad;

    @Column(nullable = false, length = 255)
    @NotNull
    private String ubicacion;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(nullable = false, length = 500)
    @NotNull
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_creacion_solicitud", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacionSolicitud;

    @Column(name = "fecha_programada", nullable = false)
    @NotNull
    @FutureOrPresent
    private LocalDateTime fechaProgramada;

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RecoleccionEntity recoleccion;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    // equals/hashCode basado en idSolicitud
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolicitudRecoleccionEntity)) return false;
        return idSolicitud != null && idSolicitud.equals(((SolicitudRecoleccionEntity) o).idSolicitud);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
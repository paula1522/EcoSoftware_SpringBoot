package com.EcoSoftware.Scrum6.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitud_recoleccion")
public class SolicitudRecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    // Usuario ciudadano que crea la solicitud
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    // Usuario que acepta la solicitud (reciclador o empresa)
    @ManyToOne
    @JoinColumn(name = "aceptada_por_id")
    private UsuarioEntity aceptadaPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false, length = 20)
    @NotNull(message = "El tipo de residuo es obligatorio")
    private TipoResiduo tipoResiduo;

    @Column(nullable = false, length = 100)
    @NotNull(message = "La cantidad es obligatoria")
    private String cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_peticion", nullable = false)
    private EstadoPeticion estadoPeticion = EstadoPeticion.Pendiente;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "La descripción es obligatoria")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "localidad", nullable = false)
    @NotNull(message = "La localidad es obligatoria")
    private Localidad localidad;

    @Column(nullable = false, length = 255)
    @NotNull(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Column(nullable = true, precision = 10, scale = 7)
private BigDecimal latitude;

@Column(nullable = true, precision = 10, scale = 7)
private BigDecimal longitude;


    @Column(name = "evidencia", nullable = false, length = 500)
    @NotNull(message = "La evidencia es obligatoria")
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_creacion_solicitud", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacionSolicitud;

    @Column(name = "fecha_programada", nullable = false)
    @NotNull(message = "La fecha programada es obligatoria")
    private LocalDateTime fechaProgramada;

    // Relación con la recolección generada (si llega a aceptarse)
    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private RecoleccionEntity recoleccion;
    //columna motivo rechazo para el estado rechazado
    @Column(name = "motivo_rechazo")
    private String motivoRechazo;


}

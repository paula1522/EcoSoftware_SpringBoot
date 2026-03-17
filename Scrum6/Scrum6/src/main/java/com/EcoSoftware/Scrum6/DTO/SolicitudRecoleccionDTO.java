package com.EcoSoftware.Scrum6.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;

import lombok.Data;

@Data
public class SolicitudRecoleccionDTO {
    private Long idSolicitud;
    private Long usuarioId;          // Id del ciudadano que crea la solicitud
    private Long aceptadaPorId;      // Id del usuario que acepta la solicitud (empresa/reciclador)

    private TipoResiduo tipoResiduo;
    private String cantidad;
    private EstadoPeticion estadoPeticion;
    private String descripcion;
    private Localidad localidad;
    private String ubicacion;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String evidencia;
    private OffsetDateTime fechaCreacionSolicitud;
    private LocalDateTime fechaProgramada;

    private Long recoleccionId;      // Relación con la recolección generada
    private String motivoRechazo;// Motivo de rechazo si la solicitud es rechazada

}

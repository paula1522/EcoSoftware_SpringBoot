package com.EcoSoftware.Scrum6.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SolicitudRecoleccionDTO {

    private Long idSolicitud;

    private Long usuarioId;
    private Long aceptadaPorId;

    @NotNull(message = "El tipo de residuo es obligatorio")
    private TipoResiduo tipoResiduo;

    @NotBlank(message = "La cantidad es obligatoria")
    @Size(max = 100)
    private String cantidad;

    private EstadoPeticion estadoPeticion;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La localidad es obligatoria")
    private Localidad localidad;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotBlank(message = "La evidencia es obligatoria")
    private String evidencia;

    private OffsetDateTime fechaCreacionSolicitud;

    @NotNull(message = "La fecha programada es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o futura")
    private LocalDateTime fechaProgramada;

    private Long recoleccionId;
    private String motivoRechazo;
}
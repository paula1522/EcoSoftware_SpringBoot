package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class RecoleccionDTO {
    private Long idRecoleccion;
    private Long solicitudId;        // ID de la solicitud origen
    private Long recolectorId;       // ID del usuario (empresa/reciclador) que aceptó
    private Long rutaId;             // ID de la ruta (puede ser null)

    private EstadoRecoleccion estado;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
private LocalDateTime fechaRecoleccion;
    private Integer ordenParada;
private String direccion;
private BigDecimal latitud;
private BigDecimal longitud;
    private String observaciones;
    private String evidencia;
    private OffsetDateTime fechaCreacionRecoleccion;
}

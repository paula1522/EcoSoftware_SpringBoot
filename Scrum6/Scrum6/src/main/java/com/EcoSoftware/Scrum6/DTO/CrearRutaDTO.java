package com.EcoSoftware.Scrum6.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CrearRutaDTO {
    private String nombre;
    private String descripcion;
    private String zonasCubiertas;
    // ids de recolecciones que el recolector seleccionó para incluir en la ruta
    private List<Long> recoleccionesSeleccionadas;
    // opcional: id de la recolección que debe ser el inicio (orden = 1)
    private Long idRecoleccionInicio;
}

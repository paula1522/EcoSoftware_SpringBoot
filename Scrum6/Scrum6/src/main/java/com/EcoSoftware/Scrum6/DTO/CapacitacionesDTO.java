package com.EcoSoftware.Scrum6.DTO;

import java.time.LocalDate;
import java.util.List;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;
import lombok.Data;

public class CapacitacionesDTO {

    @Data
    public static class CapacitacionDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private String numeroDeClases;
        private String duracion;
        private String imagen;
        /**
         * Campo usado por la validación desde Excel:
         * - "ERROR: nombre repetido" -> bloqueante
         * - "WARNING: descripción repetida" -> no bloqueante
         * - "WARNING: nombre parecido a existente" -> no bloqueante
         */
        private String observacion;
    }

    @Data
    public static class ModuloDTO {
        private Long id;
        private String duracion;
        private String descripcion;
        private Long capacitacionId;
    }

    @Data
    public static class InscripcionDTO {
        private Long id;
        private LocalDate fechaDeInscripcion;
        private EstadoCurso estadoCurso;
        private Long cursoId;
        private Long usuarioId;
    }

    @Data
    public static class ProgresoDTO {
        private Long id;
        private String progresoDelCurso;
        private String modulosCompletados;
        private String tiempoInvertido;
        private Long cursoId;
        private Long usuarioId;
    }

    @Data
    public static class UploadResultDTO {
        private int totalFilasLeidas;
        private int insertadas;
        private int rechazadas;
        private int warnings;
        private List<CapacitacionDTO> errores;   // filas con ERROR (bloqueantes)
        private List<CapacitacionDTO> avisos;    // filas con WARNING (no bloqueantes)
        private String mensaje;                  // mensaje general
    }
}

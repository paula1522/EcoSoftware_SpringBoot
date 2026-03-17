package com.EcoSoftware.Scrum6.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class NoticiaDTO {
     private String titulo;
    private String descripcion;
    private String imagen;
    private String url;
    private String fecha;
    private String fuente;
    
}

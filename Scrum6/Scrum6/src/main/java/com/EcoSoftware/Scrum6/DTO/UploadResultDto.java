package com.EcoSoftware.Scrum6.DTO;
import com.EcoSoftware.Scrum6.Entity.CapacitacionEntity;
import lombok.Data;

import java.util.List;

@Data
public class UploadResultDto {

    private int totalFilasLeidas;
    private int insertadas;
    private int rechazadas;
    private int warnings;

    private List<CapacitacionEntity> errores;
    private List<CapacitacionEntity> avisos;

    private String mensaje;
}


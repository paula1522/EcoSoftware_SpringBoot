package com.EcoSoftware.Scrum6.Exception;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.CapacitacionDTO;
import java.util.List;

public class ValidacionCapacitacionException extends RuntimeException {

    private final List<CapacitacionDTO> duplicadas;

    public ValidacionCapacitacionException(String message, List<CapacitacionDTO> duplicadas) {
        super(message);
        this.duplicadas = duplicadas;
    }

    public List<CapacitacionDTO> getDuplicadas() {
        return duplicadas;
    }
}

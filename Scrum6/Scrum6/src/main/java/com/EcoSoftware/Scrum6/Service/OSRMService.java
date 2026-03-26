package com.EcoSoftware.Scrum6.Service;

import java.util.List;

import com.EcoSoftware.Scrum6.DTO.OSRMResponseDTO;

public interface OSRMService {

    OSRMResponseDTO calcularRuta(List<double[]> coordenadas);

}
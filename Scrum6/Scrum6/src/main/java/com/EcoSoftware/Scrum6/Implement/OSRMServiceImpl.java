package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.OSRMResponseDTO;
import com.EcoSoftware.Scrum6.Service.OSRMService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OSRMServiceImpl implements OSRMService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public OSRMResponseDTO calcularRuta(List<double[]> coordenadas) {

        if (coordenadas == null || coordenadas.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 puntos");
        }

        String coords = coordenadas.stream()
                .map(c -> c[1] + "," + c[0]) // OSRM usa lon,lat
                .collect(Collectors.joining(";"));

        String url = "http://router.project-osrm.org/route/v1/driving/"
                + coords
                + "?overview=full&geometries=polyline";

        try {

            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode route = root.path("routes").get(0);

            double distanciaMetros = route.path("distance").asDouble();
            double duracionSegundos = route.path("duration").asDouble();
            String geometria = route.path("geometry").asText();

            OSRMResponseDTO dto = new OSRMResponseDTO();
            dto.setDistancia(distanciaMetros / 1000); // km
            dto.setDuracion(duracionSegundos / 60);   // minutos
            dto.setGeometria(geometria);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Error calculando ruta con OSRM", e);
        }
    }
}
package com.EcoSoftware.Scrum6.Service;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);

    private final RestTemplate restTemplate;
    private final String nominatimBaseUrl;
    private final String mapsCoBaseUrl;
    private final String userAgent;
    private final String nominatimEmail;

    public GeocodingService(RestTemplate restTemplate,
                            @Value("${app.geocoding.nominatim-url:https://nominatim.openstreetmap.org/search}") String nominatimBaseUrl,
                            @Value("${app.geocoding.mapsco-url:https://geocode.maps.co/search}") String mapsCoBaseUrl,
                            @Value("${app.geocoding.user-agent:EcoSoftwareScrum6/1.0}") String userAgent,
                            @Value("${app.geocoding.email:ecosoftwaresrc@gmail.com}") String nominatimEmail) {
        this.restTemplate = restTemplate;
        this.nominatimBaseUrl = nominatimBaseUrl;
        this.mapsCoBaseUrl = mapsCoBaseUrl;
        this.userAgent = userAgent;
        this.nominatimEmail = nominatimEmail;
    }

    public Optional<LatLng> obtenerCoordenadas(String direccion) {
        if (!StringUtils.hasText(direccion)) {
            return Optional.empty();
        }

        Optional<LatLng> primaria = consultarNominatim(direccion);
        if (primaria.isPresent()) {
            return primaria;
        }
        return consultarMapsCo(direccion);
    }

    private Optional<LatLng> consultarNominatim(String direccion) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(nominatimBaseUrl)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("addressdetails", 0)
                    .queryParam("q", direccion);
            if (StringUtils.hasText(nominatimEmail)) {
                builder.queryParam("email", nominatimEmail);
            }

            URI uri = builder.build().encode().toUri();
            RequestEntity<Void> request = RequestEntity.get(uri)
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .build();

            ResponseEntity<NominatimResponse[]> response = restTemplate.exchange(request, NominatimResponse[].class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().length == 0) {
                log.warn("Nominatim sin resultados para: {}", direccion);
                return Optional.empty();
            }
            NominatimResponse result = response.getBody()[0];
            return Optional.of(new LatLng(Double.parseDouble(result.lat()), Double.parseDouble(result.lon())));
        } catch (Exception ex) {
            log.error("Error en Nominatim para '{}': {}", direccion, ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<LatLng> consultarMapsCo(String direccion) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(mapsCoBaseUrl)
                    .queryParam("q", direccion)
                    .build()
                    .encode()
                    .toUri();

            RequestEntity<Void> request = RequestEntity.get(uri)
                    .header(HttpHeaders.USER_AGENT, userAgent)
                    .build();

            ResponseEntity<MapsCoResponse[]> response = restTemplate.exchange(request, MapsCoResponse[].class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().length == 0) {
                log.warn("Fallback maps.co sin resultados para: {}", direccion);
                return Optional.empty();
            }
            MapsCoResponse result = response.getBody()[0];
            return Optional.of(new LatLng(Double.parseDouble(result.lat()), Double.parseDouble(result.lon())));
        } catch (Exception ex) {
            log.error("Error en fallback maps.co para '{}': {}", direccion, ex.getMessage());
            return Optional.empty();
        }
    }

    public record LatLng(double latitud, double longitud) {}

    private record NominatimResponse(String lat, String lon) {}

    private record MapsCoResponse(String lat, String lon) {}
}

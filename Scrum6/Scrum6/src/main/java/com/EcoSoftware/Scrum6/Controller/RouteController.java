package com.EcoSoftware.Scrum6.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private static final String OSRM_BASE = "https://router.project-osrm.org/route/v1/driving/";

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<String> getRoute(
            @RequestParam String origin,
            @RequestParam String dest,
            @RequestParam(required = false) String overview,
            @RequestParam(required = false) String geometries,
            @RequestParam(required = false) String steps,
            @RequestParam(required = false) String alternatives) {

        // Construir URL OSRM: origin;dest
        StringBuilder url = new StringBuilder(OSRM_BASE);
        url.append(origin).append(";").append(dest);

        // Añadir parámetros opcionales
        StringBuilder query = new StringBuilder();
        if (overview != null) query.append("&overview=").append(overview);
        if (geometries != null) query.append("&geometries=").append(geometries);
        if (steps != null) query.append("&steps=").append(steps);
        if (alternatives != null) query.append("&alternatives=").append(alternatives);

        if (query.length() > 0) {
            url.append("?").append(query.toString().substring(1)); // remove first &
        }

        ResponseEntity<String> resp = restTemplate.getForEntity(url.toString(), String.class);
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }
}

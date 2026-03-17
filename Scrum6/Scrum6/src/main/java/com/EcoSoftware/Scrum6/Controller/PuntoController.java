package com.EcoSoftware.Scrum6.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.Entity.PuntoReciclaje;
import com.EcoSoftware.Scrum6.Repository.PuntoRepository;
import com.EcoSoftware.Scrum6.Service.GeocodingService;
import com.EcoSoftware.Scrum6.Service.GeocodingService.LatLng;

@RestController
@RequestMapping("/api/puntos")
public class PuntoController {

    @Autowired
    private PuntoRepository puntoRepository;

    @Autowired
    private GeocodingService geocodingService;

    @GetMapping
    public ResponseEntity<?> listar() {
        List<PuntoReciclaje> lista = puntoRepository.findAll();
        boolean huboActualizaciones = false;
        for (PuntoReciclaje punto : lista) {
            if (completarCoordenadas(punto)) {
                huboActualizaciones = true;
            }
        }
        if (huboActualizaciones) {
            puntoRepository.saveAll(lista);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", lista);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<PuntoReciclaje> opt = puntoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Punto no encontrado"));
        }
        PuntoReciclaje punto = opt.get();
        if (completarCoordenadas(punto)) {
            puntoRepository.save(punto);
        }
        return ResponseEntity.ok(Map.of("success", true, "data", punto));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PuntoReciclaje punto) {
        normalizarCamposEntrada(punto);
        if (esDuplicado(punto.getNombre(), punto.getDireccion(), null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", "Ya existe un punto con el mismo nombre y dirección."));
        }
        completarCoordenadas(punto);
        if (!tieneCoordenadasValidas(punto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", "No se pudo obtener coordenadas para la dirección indicada. Corrige la dirección o envía latitud y longitud explícitamente."));
        }
        PuntoReciclaje creado = puntoRepository.save(punto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody PuntoReciclaje cambios) {
        Optional<PuntoReciclaje> opt = puntoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Punto no encontrado"));
        }
        PuntoReciclaje p = opt.get();
        p.setNombre(cambios.getNombre());
        p.setDireccion(cambios.getDireccion());
        p.setHorario(cambios.getHorario());
        p.setTipoResiduo(cambios.getTipoResiduo());
        p.setDescripcion(cambios.getDescripcion());
        p.setLatitud(cambios.getLatitud());
        p.setLongitud(cambios.getLongitud());
        p.setUsuarioId(cambios.getUsuarioId());
        p.setImagen(cambios.getImagen());
        normalizarCamposEntrada(p);
        if (esDuplicado(p.getNombre(), p.getDireccion(), p.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Ya existe un punto con el mismo nombre y dirección."));
        }
        completarCoordenadas(p);
        if (!tieneCoordenadasValidas(p)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", "No se pudo obtener coordenadas para la dirección indicada. Corrige la dirección o envía latitud y longitud explícitamente."));
        }
        PuntoReciclaje actualizado = puntoRepository.save(p);
        return ResponseEntity.ok(Map.of("success", true, "data", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<PuntoReciclaje> opt = puntoRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Punto no encontrado"));
        }
        puntoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    private boolean completarCoordenadas(PuntoReciclaje punto) {
        boolean faltanCoordenadas = !tieneCoordenadasValidas(punto);
        if (faltanCoordenadas && StringUtils.hasText(punto.getDireccion())) {
            Optional<LatLng> resultado = geocodingService.obtenerCoordenadas(punto.getDireccion());
            resultado.ifPresent(latLng -> {
                punto.setLatitud(latLng.latitud());
                punto.setLongitud(latLng.longitud());
            });
            return resultado.isPresent();
        }
        return false;
    }

    private boolean tieneCoordenadasValidas(PuntoReciclaje punto) {
        return punto.getLatitud() != null && punto.getLongitud() != null;
    }

    private void normalizarCamposEntrada(PuntoReciclaje punto) {
        punto.setNombre(trimOrNull(punto.getNombre()));
        punto.setDireccion(trimOrNull(punto.getDireccion()));
    }

    private String trimOrNull(String valor) {
        return valor != null ? valor.trim() : null;
    }

    private boolean esDuplicado(String nombre, String direccion, Long idExcluir) {
        if (!StringUtils.hasText(nombre) || !StringUtils.hasText(direccion)) {
            return false;
        }
        if (idExcluir == null) {
            return puntoRepository.existsByNombreIgnoreCaseAndDireccionIgnoreCase(nombre, direccion);
        }
        return puntoRepository.existsByNombreIgnoreCaseAndDireccionIgnoreCaseAndIdNot(nombre, direccion, idExcluir);
    }
}

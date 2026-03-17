package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.EmailService;
import com.EcoSoftware.Scrum6.Service.NotificacionSolicitudesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificacionSolicitudesServiceImpl implements NotificacionSolicitudesService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitudRecoleccionRepository solicitudRepository;

    @Autowired
    private EmailService emailService;

    // ============================
    //   ENVÍO DIARIO PROGRAMADO
    // ============================
   @Override
@Scheduled(cron = "0 19 21 * * *", zone = "America/Bogota")
public void enviarCorreosDiarios() {

    List<UsuarioEntity> usuarios = usuarioRepository.findByEstadoTrue();

    // Filtrar solo EMPRESA o RECICLADOR
    List<UsuarioEntity> recolectores = usuarios.stream()
            .filter(this::esRecolector)
            .toList();

    if (recolectores.isEmpty()) return;

    // Obtener todas las solicitudes pendientes
    List<SolicitudRecoleccionEntity> pendientes =
            solicitudRepository.findByEstadoPeticion(EstadoPeticion.Pendiente);

    if (pendientes.isEmpty()) return;

    // Filtrar solicitudes relevantes según las 3 reglas
    List<SolicitudRecoleccionEntity> relevantes = pendientes.stream()
            .filter(s -> recolectores.stream().anyMatch(u ->
                    coincideLocalidad(u, s) &&
                    cumpleCantidadMinima(u, s)
            ))
            .sorted(Comparator.comparing(SolicitudRecoleccionEntity::getFechaProgramada))
            .limit(10)
            .toList();

    if (relevantes.isEmpty()) return;

    // Construir lista de correos BCC
    List<String> correosBCC = recolectores.stream()
            .map(UsuarioEntity::getCorreo)
            .toList();

    // Variables para Thymeleaf
    Map<String, Object> variables = new HashMap<>();
    variables.put("solicitudes", relevantes);

    // Enviar correo único con todas las solicitudes relevantes
    emailService.enviarCorreoConTemplateBCC(
            correosBCC,
            "Solicitudes de recolección más relevantes",
            "email-notificacionSolicitud",
            variables
    );
}

    // ============================
    //   VALIDACIONES POR USUARIO
    // ============================

    private boolean esRecolector(UsuarioEntity u) {
        String rol = u.getRol().getNombre().toUpperCase();
        return rol.contains("EMPRESA") || rol.contains("RECICLADOR");
    }

    public List<SolicitudRecoleccionEntity> filtrarSolicitudesParaUsuario(UsuarioEntity usuario) {

        List<SolicitudRecoleccionEntity> pendientes =
                solicitudRepository.findByEstadoPeticion(EstadoPeticion.Pendiente);

        return pendientes.stream()
                .filter(s -> coincideLocalidad(usuario, s))
                .filter(s -> coincideTipo(usuario, s))
                .filter(s -> cumpleCantidadMinima(usuario, s))
                .sorted(Comparator.comparing(SolicitudRecoleccionEntity::getFechaProgramada))
                .limit(10)
                .toList();
    }

    private boolean coincideLocalidad(UsuarioEntity u, SolicitudRecoleccionEntity s) {
        return (u.getLocalidad() != null &&
                s.getLocalidad().name().equalsIgnoreCase(u.getLocalidad()))
                ||
                (u.getZona_de_trabajo() != null &&
                        s.getLocalidad().name().equalsIgnoreCase(u.getZona_de_trabajo()));
    }

    private boolean coincideTipo(UsuarioEntity u, SolicitudRecoleccionEntity s) {
        return u.getTipoMaterial() == null ||
                u.getTipoMaterial().equalsIgnoreCase(s.getTipoResiduo().name());
    }

    private boolean cumpleCantidadMinima(UsuarioEntity u, SolicitudRecoleccionEntity s) {
        if (u.getCantidad_minima() == null) return true;

        try {
            int cantidad = extraerNumero(s.getCantidad());
            return cantidad >= u.getCantidad_minima();
        } catch (Exception e) {
            return false;
        }
    }

    private int extraerNumero(String texto) {
        return Integer.parseInt(texto.replaceAll("[^0-9]", ""));
    }

    
}

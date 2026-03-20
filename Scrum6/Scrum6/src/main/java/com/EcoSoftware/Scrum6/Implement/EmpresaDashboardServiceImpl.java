package com.EcoSoftware.Scrum6.Implement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMaterialMesDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMaterialDTO;
import com.EcoSoftware.Scrum6.DTO.EmpresaDashboardMesDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.EmpresaDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpresaDashboardServiceImpl implements EmpresaDashboardService {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-CO");
    private static final Pattern NUMERO_PATTERN = Pattern.compile("-?\\d+[\\d.,]*");
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRecoleccionRepository solicitudRepository;

    @Override
    public List<EmpresaDashboardEstadoDTO> obtenerSolicitudesPorEstado(String correoEmpresa) {
        UsuarioEntity empresa = obtenerEmpresaAutenticada(correoEmpresa);

        return solicitudRepository.findByAceptadaPor_IdUsuario(empresa.getIdUsuario()).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        this::resolverEstadoDashboard,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new EmpresaDashboardEstadoDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<EmpresaDashboardMesDTO> obtenerSolicitudesMensuales(String correoEmpresa) {
        UsuarioEntity empresa = obtenerEmpresaAutenticada(correoEmpresa);

        Map<Month, Long> solicitudesPorMes = solicitudRepository.findByAceptadaPor_IdUsuario(empresa.getIdUsuario()).stream()
                .filter(solicitud -> solicitud.getFechaCreacionSolicitud() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        solicitud -> solicitud.getFechaCreacionSolicitud().getMonth(),
                        java.util.TreeMap::new,
                        java.util.stream.Collectors.counting()));

        return solicitudesPorMes.entrySet().stream()
                .map(entry -> new EmpresaDashboardMesDTO(formatearMes(entry.getKey()), entry.getValue()))
                .toList();
    }

    @Override
    public List<EmpresaDashboardMaterialDTO> obtenerMaterialesPorTipo(String correoEmpresa) {
        UsuarioEntity empresa = obtenerEmpresaAutenticada(correoEmpresa);

        Map<String, Double> materialesPorTipo = solicitudRepository.findByAceptadaPor_IdUsuario(empresa.getIdUsuario()).stream()
            .filter(this::esSolicitudGestionable)
                .collect(java.util.stream.Collectors.groupingBy(
                        solicitud -> formatearEnum(solicitud.getTipoResiduo().name()),
                        LinkedHashMap::new,
                java.util.stream.Collectors.summingDouble(this::resolverCantidadMaterialReal)));

        return materialesPorTipo.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
                .map(entry -> new EmpresaDashboardMaterialDTO(entry.getKey(), redondear(entry.getValue())))
                .toList();
    }

    @Override
        public List<EmpresaDashboardMaterialMesDTO> obtenerMaterialGestionadoPorMes(String correoEmpresa) {
        UsuarioEntity empresa = obtenerEmpresaAutenticada(correoEmpresa);

        Map<Month, Double> materialPorMes = solicitudRepository.findByAceptadaPor_IdUsuario(empresa.getIdUsuario()).stream()
                .filter(this::esSolicitudGestionable)
                .filter(solicitud -> solicitud.getFechaCreacionSolicitud() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        solicitud -> solicitud.getFechaCreacionSolicitud().getMonth(),
                        java.util.TreeMap::new,
                java.util.stream.Collectors.summingDouble(this::resolverCantidadMaterialReal)));

        return materialPorMes.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(entry -> new EmpresaDashboardMaterialMesDTO(formatearMes(entry.getKey()), redondear(entry.getValue())))
                .toList();
    }

    private UsuarioEntity obtenerEmpresaAutenticada(String correoEmpresa) {
        UsuarioEntity empresa = usuarioRepository.findByCorreoAndEstadoTrue(correoEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa autenticada no encontrada"));

        if (!"Empresa".equalsIgnoreCase(empresa.getRol().getNombre())) {
            throw new RuntimeException("El usuario autenticado no corresponde a una empresa");
        }

        return empresa;
    }

    private String resolverEstadoDashboard(SolicitudRecoleccionEntity solicitud) {
        RecoleccionEntity recoleccion = solicitud.getRecoleccion();
        if (recoleccion == null) {
            return formatearEnum(solicitud.getEstadoPeticion().name());
        }

        return switch (recoleccion.getEstado()) {
            case Pendiente -> "Aceptada";
            case En_Progreso -> "En progreso";
            case Completada -> "Completada";
            case Fallida -> "Fallida";
            case Cancelada -> "Cancelada";
        };
    }

    private boolean esSolicitudGestionable(SolicitudRecoleccionEntity solicitud) {
        RecoleccionEntity recoleccion = solicitud.getRecoleccion();
        if (recoleccion == null) {
            return solicitud.getAceptadaPor() != null;
        }

        return recoleccion.getEstado() != EstadoRecoleccion.Cancelada
                && recoleccion.getEstado() != EstadoRecoleccion.Fallida;
    }

    private double resolverCantidadMaterialReal(SolicitudRecoleccionEntity solicitud) {
        double cantidadNumerica = extraerCantidadNumerica(solicitud.getCantidad());
        if (cantidadNumerica > 0) {
            return cantidadNumerica;
        }

        return 1.0;
    }

    private double extraerCantidadNumerica(String valor) {
        if (valor == null || valor.isBlank()) {
            return 0.0;
        }

        Matcher matcher = NUMERO_PATTERN.matcher(valor.replace(" ", ""));
        if (!matcher.find()) {
            return 0.0;
        }

        String numero = matcher.group();
        if (numero.contains(",") && numero.contains(".")) {
            numero = numero.replace(".", "").replace(',', '.');
        } else if (numero.contains(",")) {
            numero = numero.replace(',', '.');
        }

        return Double.parseDouble(numero);
    }

    private String formatearMes(Month month) {
        return capitalizar(month.getDisplayName(TextStyle.FULL, LOCALE_ES));
    }

    private String formatearEnum(String valor) {
        return capitalizar(valor.replace('_', ' ').toLowerCase(LOCALE_ES));
    }

    private String capitalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return valor;
        }
        return valor.substring(0, 1).toUpperCase(LOCALE_ES) + valor.substring(1);
    }

    private Double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
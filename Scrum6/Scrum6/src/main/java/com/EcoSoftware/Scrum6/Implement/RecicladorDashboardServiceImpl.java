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

import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardCumplimientoDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardMaterialDTO;
import com.EcoSoftware.Scrum6.DTO.RecicladorDashboardPeriodoDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RecicladorDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecicladorDashboardServiceImpl implements RecicladorDashboardService {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-CO");
    private static final Pattern NUMERO_PATTERN = Pattern.compile("-?\\d+[\\d.,]*");

    private final UsuarioRepository usuarioRepository;
    private final RecoleccionRepository recoleccionRepository;

    @Override
    public List<RecicladorDashboardPeriodoDTO> obtenerRecoleccionesPorPeriodo(String correoReciclador) {
        UsuarioEntity reciclador = obtenerRecicladorAutenticado(correoReciclador);

        // Consulta optimizada - agrupación en BD
        List<Object[]> resultados = recoleccionRepository.obtenerMesesReciclador(reciclador.getIdUsuario());

        return resultados.stream()
                .map(row -> {
                    int anio = ((Number) row[0]).intValue();
                    int mes = ((Number) row[1]).intValue();
                    long cantidad = ((Number) row[2]).longValue();
                    String nombreMes = formatearMesNumerico(mes) + " " + anio;
                    return new RecicladorDashboardPeriodoDTO(nombreMes, cantidad);
                })
                .toList();
    }

    @Override
    public List<RecicladorDashboardEstadoDTO> obtenerRecoleccionesPorEstado(String correoReciclador) {
        UsuarioEntity reciclador = obtenerRecicladorAutenticado(correoReciclador);

        // Consulta optimizada - agrupación en BD
        List<Object[]> resultados = recoleccionRepository.obtenerEstadosReciclador(reciclador.getIdUsuario());

        return resultados.stream()
                .map(row -> new RecicladorDashboardEstadoDTO(
                    formatearEnum(((Enum<?>) row[0]).name()),
                    ((Number) row[1]).longValue()
                ))
                .toList();
    }

    @Override
    public List<RecicladorDashboardCumplimientoDTO> obtenerCumplimiento(String correoReciclador) {
        UsuarioEntity reciclador = obtenerRecicladorAutenticado(correoReciclador);

        // Consultas optimizadas - sin cargar entidades completas
        Long totalRecolecciones = recoleccionRepository.obtenerTotalRecolecciones(reciclador.getIdUsuario());
        Long recoleccionesCompletadas = recoleccionRepository.obtenerRecoleccionesCompletadas(reciclador.getIdUsuario());

        double porcentajeCumplimiento = totalRecolecciones > 0 
                ? (double) recoleccionesCompletadas / totalRecolecciones * 100 
                : 0.0;

        return List.of(
            new RecicladorDashboardCumplimientoDTO(
                "Cumplimiento total",
                redondear(porcentajeCumplimiento),
                recoleccionesCompletadas,
                totalRecolecciones
            )
        );
    }

    @Override
    public List<RecicladorDashboardMaterialDTO> obtenerMaterialRecolectado(String correoReciclador) {
        UsuarioEntity reciclador = obtenerRecicladorAutenticado(correoReciclador);

        // Consulta optimizada - solo campos necesarios
        List<Object[]> resultados = recoleccionRepository.obtenerMaterialRecolectadoReciclador(reciclador.getIdUsuario());

        Map<String, Double> materialPorTipo = new LinkedHashMap<>();
        for (Object[] row : resultados) {
            String tipoResiduo = formatearEnum(((Enum<?>) row[0]).name());
            double cantidad = extraerCantidadNumerica((String) row[1]);
            materialPorTipo.merge(tipoResiduo, cantidad, Double::sum);
        }

        return materialPorTipo.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new RecicladorDashboardMaterialDTO(entry.getKey(), redondear(entry.getValue())))
                .toList();
    }

    private UsuarioEntity obtenerRecicladorAutenticado(String correoReciclador) {
        UsuarioEntity reciclador = usuarioRepository.findByCorreoAndEstadoTrue(correoReciclador)
                .orElseThrow(() -> new RuntimeException("Reciclador autenticado no encontrado"));

        if (!"Reciclador".equalsIgnoreCase(reciclador.getRol().getNombre())) {
            throw new RuntimeException("El usuario autenticado no corresponde a un reciclador");
        }

        return reciclador;
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

    private String formatearMesNumerico(int mes) {
        try {
            return capitalizar(Month.of(mes).getDisplayName(TextStyle.FULL, LOCALE_ES));
        } catch (Exception e) {
            return "Mes " + mes;
        }
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

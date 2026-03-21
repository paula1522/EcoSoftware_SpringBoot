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

import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardEstadoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardImpactoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardResiduoDTO;
import com.EcoSoftware.Scrum6.DTO.CiudadanoDashboardTiempoDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.CiudadanoDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiudadanoDashboardServiceImpl implements CiudadanoDashboardService {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-CO");
    private static final Pattern NUMERO_PATTERN = Pattern.compile("-?\\d+[\\d.,]*");
    
    // Factores de conversión ambiental
    private static final double KG_TO_CO2_PLASTICO = 5.0;      // 1 kg plástico evita 5 kg CO2
    private static final double KG_TO_CO2_PAPEL = 2.5;         // 1 kg papel evita 2.5 kg CO2
    private static final double KG_TO_CO2_VIDRIO = 1.0;        // 1 kg vidrio evita 1 kg CO2
    private static final double KG_TO_CO2_METAL = 8.0;         // 1 kg metal evita 8 kg CO2
    private static final double KG_TO_ARBOLES = 0.01;          // 20 kg = 1 árbol salvado
    
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRecoleccionRepository solicitudRepository;

    @Override
    public List<CiudadanoDashboardEstadoDTO> obtenerSolicitudesPorEstado(String correoCiudadano) {
        UsuarioEntity ciudadano = obtenerCiudadanoAutenticado(correoCiudadano);
        
        // Consulta optimizada - agrupación en BD
        List<Object[]> resultados = solicitudRepository.obtenerEstadosCiudadano(ciudadano.getIdUsuario());
        
        return resultados.stream()
                .map(row -> new CiudadanoDashboardEstadoDTO(
                    formatearEnum(((Enum<?>) row[0]).name()),
                    ((Number) row[1]).longValue()
                ))
                .toList();
    }

    @Override
    public List<CiudadanoDashboardTiempoDTO> obtenerSolicitudesPorTiempo(String correoCiudadano) {
        UsuarioEntity ciudadano = obtenerCiudadanoAutenticado(correoCiudadano);
        
        // Consulta optimizada - agrupación en BD
        List<Object[]> resultados = solicitudRepository.obtenerMesesCiudadano(ciudadano.getIdUsuario());
        
        return resultados.stream()
                .map(row -> {
                    int anio = ((Number) row[0]).intValue();
                    int mes = ((Number) row[1]).intValue();
                    long cantidad = ((Number) row[2]).longValue();
                    String nombreMes = formatearMesNumerico(mes) + " " + anio;
                    return new CiudadanoDashboardTiempoDTO(nombreMes, cantidad);
                })
                .toList();
    }

    @Override
    public List<CiudadanoDashboardResiduoDTO> obtenerResiduosPorTipo(String correoCiudadano) {
        UsuarioEntity ciudadano = obtenerCiudadanoAutenticado(correoCiudadano);
        
        // Consulta optimizada - proyección minimal
        List<Object[]> resultados = solicitudRepository.obtenerResiduosPorTipoCiudadano(ciudadano.getIdUsuario());
        
        Map<String, Double> residuosPorTipo = new LinkedHashMap<>();
        for (Object[] row : resultados) {
            String tipoResiduo = formatearEnum(((Enum<?>) row[0]).name());
            double cantidad = extraerCantidadNumerica((String) row[1]);
            residuosPorTipo.merge(tipoResiduo, cantidad, Double::sum);
        }
        
        return residuosPorTipo.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new CiudadanoDashboardResiduoDTO(entry.getKey(), redondear(entry.getValue())))
                .toList();
    }

    @Override
    public List<CiudadanoDashboardImpactoDTO> obtenerImpactoAmbiental(String correoCiudadano) {
        UsuarioEntity ciudadano = obtenerCiudadanoAutenticado(correoCiudadano);
        
        // Consulta optimizada - solo campos necesarios
        List<Object[]> datos = solicitudRepository.obtenerDatosImpactoCiudadano(ciudadano.getIdUsuario());
        
        double totalKgReciclados = 0;
        double totalCO2Evitado = 0;
        
        for (Object[] row : datos) {
            String tipoResiduo = ((Enum<?>) row[0]).name();
            String cantidad = (String) row[1];
            double kg = extraerCantidadNumerica(cantidad);
            
            totalKgReciclados += kg;
            totalCO2Evitado += calcularCO2EvitadoPorTipo(tipoResiduo, kg);
        }
        
        double arboleSalvados = totalKgReciclados * KG_TO_ARBOLES;
        
        return List.of(
            new CiudadanoDashboardImpactoDTO("Kg reciclados", redondear(totalKgReciclados), "kg"),
            new CiudadanoDashboardImpactoDTO("CO2 evitado", redondear(totalCO2Evitado), "kg CO2"),
            new CiudadanoDashboardImpactoDTO("Árboles salvados", redondear(arboleSalvados), "árboles")
        );
    }

    private UsuarioEntity obtenerCiudadanoAutenticado(String correoCiudadano) {
        UsuarioEntity ciudadano = usuarioRepository.findByCorreoAndEstadoTrue(correoCiudadano)
                .orElseThrow(() -> new RuntimeException("Ciudadano autenticado no encontrado"));

        if (!"Ciudadano".equalsIgnoreCase(ciudadano.getRol().getNombre())) {
            throw new RuntimeException("El usuario autenticado no corresponde a un ciudadano");
        }

        return ciudadano;
    }

    private double calcularCO2EvitadoPorTipo(String tipoResiduo, double cantidad) {
        return switch (tipoResiduo) {
            case "Plástico" -> cantidad * KG_TO_CO2_PLASTICO;
            case "Papel" -> cantidad * KG_TO_CO2_PAPEL;
            case "Vidrio" -> cantidad * KG_TO_CO2_VIDRIO;
            case "Metal" -> cantidad * KG_TO_CO2_METAL;
            default -> cantidad * 2.0; // Promedio general
        };
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

    private String formatearMesNumerico(int mes) {
        try {
            return capitalizar(Month.of(mes).getDisplayName(TextStyle.FULL, LOCALE_ES));
        } catch (Exception e) {
            return "Mes " + mes;
        }
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

package com.EcoSoftware.Scrum6.Implement;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.EcoSoftware.Scrum6.DTO.CrearRutaDTO;
import com.EcoSoftware.Scrum6.DTO.ParadaDTO;
import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Enums.EstadoRuta;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RutaRecoleccionServiceImpl implements RutaRecoleccionService {

    private final RutaRecoleccionRepository rutaRepository;
    private final RecoleccionRepository recoleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;

    @Value("${osrm.base-url:https://router.project-osrm.org}")
    private String osrmBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===============================
    // CREAR RUTA
    // ===============================
    @Override
    @Transactional
    public RutaRecoleccionDTO crearRuta(CrearRutaDTO dto, Long recolectorId) {
        UsuarioEntity recolector = usuarioRepository.findById(recolectorId)
                .orElseThrow(() -> new EntityNotFoundException("Recolector no encontrado"));

        List<RecoleccionEntity> recolecciones = recoleccionRepository.findAllById(dto.getRecoleccionIds());
        if (recolecciones.size() != dto.getRecoleccionIds().size()) {
            throw new IllegalArgumentException("Alguna recolección no existe");
        }

        for (RecoleccionEntity rec : recolecciones) {
            if (!rec.getRecolector().getIdUsuario().equals(recolectorId)) {
                throw new IllegalArgumentException("Recolección " + rec.getIdRecoleccion() + " no pertenece al recolector");
            }
            if (rec.getEstado() == EstadoRecoleccion.Completada || rec.getEstado() == EstadoRecoleccion.Cancelada) {
                throw new IllegalArgumentException("Recolección " + rec.getIdRecoleccion() + " ya está finalizada");
            }
        }

        RutaRecoleccionEntity ruta = new RutaRecoleccionEntity();
        ruta.setNombre(dto.getNombre());
        ruta.setRecolector(recolector);
        ruta.setEstado(EstadoRuta.PLANIFICADA);
        ruta.setFechaCreacion(LocalDateTime.now());

        int orden = 1;
        for (RecoleccionEntity rec : recolecciones) {
            rec.setOrdenParada(orden++);
            rec.setRuta(ruta);
        }
        ruta.setRecolecciones(recolecciones);

        calcularYGuardarRuta(ruta, recolecciones);

        RutaRecoleccionEntity saved = rutaRepository.save(ruta);
        return convertirADTO(saved);
    }

    // ===============================
    // ACTUALIZAR RUTA
    // ===============================
    @Override
    @Transactional
    public RutaRecoleccionDTO actualizarRuta(Long id, RutaRecoleccionDTO dto) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        if (dto.getNombre() != null) ruta.setNombre(dto.getNombre());
        if (dto.getDistanciaTotal() != null) ruta.setDistanciaTotal(dto.getDistanciaTotal());
        if (dto.getTiempoEstimado() != null) ruta.setTiempoEstimado(dto.getTiempoEstimado());
        if (dto.getGeometriaRuta() != null) ruta.setGeometriaRuta(dto.getGeometriaRuta());

        return convertirADTO(rutaRepository.save(ruta));
    }

    // ===============================
    // INICIAR RUTA
    // ===============================
    @Override
    @Transactional
    public RutaRecoleccionDTO iniciarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        if (ruta.getEstado() != EstadoRuta.PLANIFICADA) {
            throw new IllegalStateException("Solo se puede iniciar una ruta en estado PLANIFICADA");
        }
        ruta.setEstado(EstadoRuta.EN_PROGRESO);
        return convertirADTO(rutaRepository.save(ruta));
    }

    // ===============================
    // FINALIZAR RUTA
    // ===============================
    @Override
    @Transactional
    public RutaRecoleccionDTO finalizarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        if (ruta.getEstado() != EstadoRuta.EN_PROGRESO) {
            throw new IllegalStateException("Solo se puede finalizar una ruta en progreso");
        }
        ruta.setEstado(EstadoRuta.FINALIZADA);
        return convertirADTO(rutaRepository.save(ruta));
    }

    // ===============================
    // ELIMINAR FÍSICAMENTE
    // ===============================
    @Override
    @Transactional
    public void eliminarFisicamente(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        // Desasociar recolecciones
        if (ruta.getRecolecciones() != null) {
            for (RecoleccionEntity rec : ruta.getRecolecciones()) {
                rec.setRuta(null);
                rec.setOrdenParada(null);
            }
        }
        rutaRepository.delete(ruta);
    }

    // ===============================
    // CANCELAR RUTA (eliminación lógica)
    // ===============================
    @Override
    @Transactional
    public RutaRecoleccionDTO cancelarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        if (ruta.getEstado() == EstadoRuta.FINALIZADA) {
            throw new IllegalStateException("No se puede cancelar una ruta finalizada");
        }
        ruta.setEstado(EstadoRuta.CANCELADA);
        return convertirADTO(rutaRepository.save(ruta));
    }

    // ===============================
    // OBTENER POR ID
    // ===============================
    @Override
    public RutaRecoleccionDTO obtenerPorId(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        return convertirADTO(ruta);
    }

    // ===============================
    // LISTAR TODAS
    // ===============================
    @Override
    public List<RutaRecoleccionDTO> listarTodas() {
        return rutaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // LISTAR POR ESTADO
    // ===============================
    @Override
    public List<RutaRecoleccionDTO> listarPorEstado(EstadoRuta estado) {
        return rutaRepository.findByEstado(estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // LISTAR POR RECOLECTOR
    // ===============================
    @Override
    public List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId) {
        return rutaRepository.findByRecolector_IdUsuario(recolectorId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // REPORTE EXCEL
    // ===============================
    @Override
    public void generarReporteExcel(String nombre, String estado, Long recolectorId,
                                    LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                    OutputStream os) throws IOException {
        List<RutaRecoleccionEntity> rutas = obtenerRutasConFiltros(nombre, estado, recolectorId, fechaDesde, fechaHasta);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rutas");

        // Encabezados
        String[] headers = {"ID", "Nombre", "Estado", "Recolector ID", "Recolector", "Distancia (km)", "Tiempo (min)", "Paradas", "Fecha Creación"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Datos
        int rowNum = 1;
        for (RutaRecoleccionEntity r : rutas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getIdRuta());
            row.createCell(1).setCellValue(r.getNombre());
            row.createCell(2).setCellValue(r.getEstado().name());
            row.createCell(3).setCellValue(r.getRecolector().getIdUsuario());
            row.createCell(4).setCellValue(r.getRecolector().getNombre());
            row.createCell(5).setCellValue(r.getDistanciaTotal() != null ? r.getDistanciaTotal() : 0);
            row.createCell(6).setCellValue(r.getTiempoEstimado() != null ? r.getTiempoEstimado() : 0);
            row.createCell(7).setCellValue(r.getRecolecciones() != null ? r.getRecolecciones().size() : 0);
            row.createCell(8).setCellValue(r.getFechaCreacion() != null ? r.getFechaCreacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
        }

        // Ajustar ancho
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

    // ===============================
    // REPORTE PDF
    // ===============================
    @Override
    public void generarReportePDF(String nombre, String estado, Long recolectorId,
                                  LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                  OutputStream os) throws IOException {
        List<RutaRecoleccionEntity> rutas = obtenerRutasConFiltros(nombre, estado, recolectorId, fechaDesde, fechaHasta);

        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, os);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Reporte de Rutas de Recolección", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Tabla
            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 2, 3, 2, 2, 1, 3});

            // Encabezados
            String[] headers = {"ID", "Nombre", "Estado", "Recolector ID", "Recolector", "Distancia (km)", "Tiempo (min)", "Paradas", "Fecha Creación"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Datos
            for (RutaRecoleccionEntity r : rutas) {
                table.addCell(String.valueOf(r.getIdRuta()));
                table.addCell(r.getNombre());
                table.addCell(r.getEstado().name());
                table.addCell(String.valueOf(r.getRecolector().getIdUsuario()));
                table.addCell(r.getRecolector().getNombre());
                table.addCell(String.valueOf(r.getDistanciaTotal() != null ? r.getDistanciaTotal() : 0));
                table.addCell(String.valueOf(r.getTiempoEstimado() != null ? r.getTiempoEstimado() : 0));
                table.addCell(String.valueOf(r.getRecolecciones() != null ? r.getRecolecciones().size() : 0));
                table.addCell(r.getFechaCreacion() != null ? r.getFechaCreacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error al generar PDF", e);
        }
    }

    // ===============================
    // MÉTODOS PRIVADOS AUXILIARES
    // ===============================
    private List<RutaRecoleccionEntity> obtenerRutasConFiltros(String nombre, String estado, Long recolectorId,
                                                               LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return rutaRepository.findAll().stream()
                .filter(r -> nombre == null || r.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(r -> estado == null || r.getEstado().name().equals(estado))
                .filter(r -> recolectorId == null || r.getRecolector().getIdUsuario().equals(recolectorId))
                .filter(r -> fechaDesde == null || (r.getFechaCreacion() != null && !r.getFechaCreacion().isBefore(fechaDesde)))
                .filter(r -> fechaHasta == null || (r.getFechaCreacion() != null && !r.getFechaCreacion().isAfter(fechaHasta)))
                .collect(Collectors.toList());
    }

    private void calcularYGuardarRuta(RutaRecoleccionEntity ruta, List<RecoleccionEntity> recolecciones) {
        List<double[]> coordenadas = new ArrayList<>();
        for (RecoleccionEntity rec : recolecciones) {
            BigDecimal lat = rec.getSolicitud().getLatitude();
            BigDecimal lng = rec.getSolicitud().getLongitude();
            if (lat != null && lng != null) {
                coordenadas.add(new double[]{lat.doubleValue(), lng.doubleValue()});
            } else {
                throw new IllegalArgumentException("Recolección sin coordenadas: " + rec.getIdRecoleccion());
            }
        }

        if (coordenadas.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 recolecciones con coordenadas");
        }

        String url = construirUrlTrip(coordenadas);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            if (body == null) throw new RuntimeException("Respuesta vacía de OSRM");

            Map<String, Object> json = objectMapper.readValue(body, Map.class);
            List<Map<String, Object>> trips = (List<Map<String, Object>>) json.get("trips");
            if (trips == null || trips.isEmpty()) throw new RuntimeException("No se pudo calcular la ruta");

            Map<String, Object> trip = trips.get(0);
            double distancia = ((Number) trip.get("distance")).doubleValue() / 1000.0;
            double tiempo = ((Number) trip.get("duration")).doubleValue() / 60.0;

            Map<String, Object> geometry = (Map<String, Object>) trip.get("geometry");
            List<List<Double>> coordinatesRaw = (List<List<Double>>) geometry.get("coordinates");

            List<Map<String, Double>> puntosGeometria = coordinatesRaw.stream()
                    .map(coord -> Map.of("lat", coord.get(1), "lng", coord.get(0)))
                    .collect(Collectors.toList());

            String geometriaJson = objectMapper.writeValueAsString(puntosGeometria);

            ruta.setDistanciaTotal(distancia);
            ruta.setTiempoEstimado(tiempo);
            ruta.setGeometriaRuta(geometriaJson);

        } catch (Exception e) {
            throw new RuntimeException("Error al calcular ruta con OSRM", e);
        }
    }

    private String construirUrlTrip(List<double[]> coordenadas) {
        String coordsStr = coordenadas.stream()
                .map(c -> c[1] + "," + c[0])
                .collect(Collectors.joining(";"));
        return osrmBaseUrl + "/trip/v1/driving/" + coordsStr + "?overview=full&geometries=geojson";
    }

    private RutaRecoleccionDTO convertirADTO(RutaRecoleccionEntity entity) {
        RutaRecoleccionDTO dto = new RutaRecoleccionDTO();
        dto.setIdRuta(entity.getIdRuta());
        dto.setNombre(entity.getNombre());
        dto.setEstado(entity.getEstado());
        dto.setRecolectorId(entity.getRecolector().getIdUsuario());
        dto.setDistanciaTotal(entity.getDistanciaTotal());
        dto.setTiempoEstimado(entity.getTiempoEstimado());
        dto.setGeometriaRuta(entity.getGeometriaRuta());
        dto.setFechaCreacion(entity.getFechaCreacion());

        List<RecoleccionEntity> recolecciones = entity.getRecolecciones();
        if (recolecciones != null) {
            recolecciones.sort(Comparator.comparing(RecoleccionEntity::getOrdenParada, Comparator.nullsLast(Comparator.naturalOrder())));

            dto.setRecoleccionIds(recolecciones.stream()
                    .map(RecoleccionEntity::getIdRecoleccion)
                    .collect(Collectors.toList()));

            List<ParadaDTO> paradas = recolecciones.stream()
                    .filter(rec -> rec.getSolicitud() != null)
                    .map(rec -> {
                        ParadaDTO p = new ParadaDTO();
                        p.setRecoleccionId(rec.getIdRecoleccion());
                        p.setOrdenParada(rec.getOrdenParada());
                        p.setLatitud(rec.getSolicitud().getLatitude());
                        p.setLongitud(rec.getSolicitud().getLongitude());
                        p.setEstado(rec.getEstado());
                        return p;
                    })
                    .collect(Collectors.toList());
            dto.setParadas(paradas);
        }
        return dto;
    }
}
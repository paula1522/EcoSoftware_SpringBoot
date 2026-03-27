package com.EcoSoftware.Scrum6.Implement;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import java.math.BigDecimal;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Exception.AccesoDenegadoException;
import com.EcoSoftware.Scrum6.Exception.SolicitudNotFoundException;
import com.EcoSoftware.Scrum6.Exception.UsuarioNotFoundException;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.CloudinaryService;
import com.EcoSoftware.Scrum6.Service.EmailService;
import com.EcoSoftware.Scrum6.Service.GeocodingService;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Implementación del servicio para gestionar solicitudes de recolección.
 * Contiene métodos CRUD, generación de reportes (Excel y PDF)
 * y lógica de negocio para aceptar/rechazar solicitudes.
 */
@Service
@Transactional
public class SolicitudRecoleccionServiceImpl implements SolicitudRecoleccionService {
    // ===================== MÉTODOS PARA GRÁFICOS =====================
    @Override
    public List<Object[]> obtenerRechazadasPorMotivo() {
        return solicitudRepository.obtenerRechazadasAgrupadasPorMotivo();
    }

    @Override
    public Long contarAceptadas() {
        return solicitudRepository.countAceptadas();
    }

    @Override
    public Long contarPendientes() {
        return solicitudRepository.countPendientes();
    }

    @Override
    public List<Object[]> obtenerSolicitudesPorLocalidad() {
        return solicitudRepository.obtenerSolicitudesPorLocalidad();
    }

    private final SolicitudRecoleccionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(SolicitudRecoleccionServiceImpl.class);

    public SolicitudRecoleccionServiceImpl(SolicitudRecoleccionRepository solicitudRepository,
            UsuarioRepository usuarioRepository, EmailService emailService, TemplateEngine templateEngine,
            GeocodingService geocodingService, CloudinaryService cloudinaryService) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.geocodingService = geocodingService;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Convierte una entidad de SolicitudRecoleccionEntity a
     * SolicitudRecoleccionDTO.
     * 
     * @param entity La entidad a convertir.
     * @return El DTO resultante.
     */
    private SolicitudRecoleccionDTO entityToDTO(SolicitudRecoleccionEntity entity) {
        SolicitudRecoleccionDTO dto = new SolicitudRecoleccionDTO();
        dto.setIdSolicitud(entity.getIdSolicitud());
        dto.setUsuarioId(entity.getUsuario().getIdUsuario());
        dto.setAceptadaPorId(entity.getAceptadaPor() != null ? entity.getAceptadaPor().getIdUsuario() : null);
        dto.setTipoResiduo(entity.getTipoResiduo());
        dto.setCantidad(entity.getCantidad());
        dto.setEstadoPeticion(entity.getEstadoPeticion());
        dto.setDescripcion(entity.getDescripcion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setUbicacion(entity.getUbicacion());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setEvidencia(entity.getEvidencia());
        dto.setFechaCreacionSolicitud(entity.getFechaCreacionSolicitud());
        dto.setFechaProgramada(entity.getFechaProgramada());
        dto.setRecoleccionId(entity.getRecoleccion() != null ? entity.getRecoleccion().getIdRecoleccion() : null);
        dto.setMotivoRechazo(entity.getMotivoRechazo());
        return dto;
    }

    @Async
    public void enviarEmailAsync(String to, String subject, String html) {
        try {
            emailService.enviarCorreo(to, subject, html);
        } catch (Exception e) {
            logger.error("Error al enviar email a {}: {}", to, e.getMessage());
        }
    }
    // ==========================================================
    // Métodos CRUD
    // ==========================================================

    @Override
    public SolicitudRecoleccionDTO crearSolicitud(SolicitudRecoleccionDTO dto, String correoUsuario) {

        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        if (dto.getFechaProgramada() == null) {
            throw new IllegalArgumentException("La fecha programada es obligatoria");
        }

        if (dto.getFechaProgramada().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a hoy");
        }

        if (dto.getUbicacion() == null || dto.getUbicacion().isBlank()) {
            throw new IllegalArgumentException("La ubicación es obligatoria");
        }

        SolicitudRecoleccionEntity entity = new SolicitudRecoleccionEntity();

        entity.setUsuario(usuario);
        entity.setTipoResiduo(dto.getTipoResiduo());
        entity.setCantidad(dto.getCantidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setUbicacion(dto.getUbicacion());

        geocodingService.obtenerCoordenadas(dto.getUbicacion())
                .ifPresentOrElse(coords -> {
                    entity.setLatitude(BigDecimal.valueOf(coords.latitud()));
                    entity.setLongitude(BigDecimal.valueOf(coords.longitud()));
                }, () -> {
                    throw new RuntimeException("No se pudieron obtener coordenadas");
                });

        entity.setEvidencia(dto.getEvidencia());
        entity.setFechaProgramada(dto.getFechaProgramada());
        entity.setEstadoPeticion(EstadoPeticion.Pendiente);
        entity.setFechaCreacionSolicitud(OffsetDateTime.now());

        SolicitudRecoleccionEntity saved = solicitudRepository.save(entity);

        // EMAIL (asíncrono)
        Context context = new Context();
        context.setVariable("nombre", usuario.getNombre());
        context.setVariable("idSolicitud", saved.getIdSolicitud());
        context.setVariable("estado", saved.getEstadoPeticion().name());
        context.setVariable("tipoResiduo", entity.getTipoResiduo());
        context.setVariable("cantidad", entity.getCantidad());
        context.setVariable("descripcion", entity.getDescripcion());
        context.setVariable("localidad", entity.getLocalidad());
        context.setVariable("ubicacion", entity.getUbicacion());
        context.setVariable("fechaProgramada",
                entity.getFechaProgramada() != null ? entity.getFechaProgramada().toString() : "N/A");

        String html = templateEngine.process("email-registroSolicitud", context);
        enviarEmailAsync(usuario.getCorreo(), "Solicitud registrada correctamente", html);

        return entityToDTO(saved);
    }

    @Override
    public String subirEvidencia(MultipartFile file, Long idSolicitud) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Imagen no enviada");
        }

        // Validar tipo de archivo (solo imágenes)
        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Solo se permiten imágenes");
        }

        // valida solicitud existe
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Folder y publicId para organizar en Cloudinary
        String folder = "solicitudes/" + idSolicitud;
        String publicId = "evidencia_" + System.currentTimeMillis();

        // Subir a Cloudinary y obtener URL
        String url = cloudinaryService.upload(file, folder, publicId);

        // Guardar URL en la solicitud
        solicitud.setEvidencia(url);
        solicitudRepository.save(solicitud);

        return url;
    }

    @Override
    public SolicitudRecoleccionDTO obtenerPorId(Long id) {
        SolicitudRecoleccionEntity entity = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return entityToDTO(entity);
    }

    @Override
public List<SolicitudRecoleccionDTO> listarTodas() {
    List<SolicitudRecoleccionEntity> entities = solicitudRepository.findAllWithRelations();
    return entities.stream()
            .map(this::entityToDTO)
            .collect(Collectors.toList());
}
    @Override
    public List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado) {
        return solicitudRepository.findByEstadoPeticion(estado).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId) {

        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException("Solicitud no encontrada"));

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new IllegalStateException("Solo se pueden aceptar solicitudes pendientes");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoRecolector = auth.getName();

        UsuarioEntity recolector = usuarioRepository.findByCorreo(correoRecolector)
                .orElseThrow(() -> new UsuarioNotFoundException("Recolector no encontrado"));

        // Validar rol: debe ser Recolector o Administrador
        String rol = recolector.getRol().getNombre();
        if (!"Recolector".equals(rol) && !"Administrador".equals(rol)) {
            throw new AccesoDenegadoException("No tienes permisos para aceptar solicitudes");
        }

        solicitud.setAceptadaPor(recolector);
        solicitud.setEstadoPeticion(EstadoPeticion.Aceptada);

        RecoleccionEntity recoleccion = new RecoleccionEntity();
        recoleccion.setSolicitud(solicitud);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.Pendiente);
        recoleccion.setFechaRecoleccion(solicitud.getFechaProgramada());
        solicitud.setRecoleccion(recoleccion);

        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);

        // EMAIL al ciudadano (asíncrono)
        UsuarioEntity usuarioSolicitante = solicitud.getUsuario();
        Context context = new Context();
        context.setVariable("nombre", usuarioSolicitante.getNombre());
        context.setVariable("idSolicitud", solicitud.getIdSolicitud());
        context.setVariable("nombreRecolector", recolector.getNombre());
        context.setVariable("estadoPeticion", solicitud.getEstadoPeticion().name());
        context.setVariable("fechaProgramada", solicitud.getFechaProgramada().toString());

        String html = templateEngine.process("email-aceptaSolicitud", context);
        enviarEmailAsync(usuarioSolicitante.getCorreo(), "Tu solicitud fue aceptada", html);

        return entityToDTO(saved);
    }

    @Override
    public SolicitudRecoleccionDTO rechazarSolicitud(Long solicitudId, String motivo) {
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden rechazar solicitudes pendientes");
        }

        // Guardar el motivo en la entidad
        solicitud.setMotivoRechazo(motivo != null && !motivo.isBlank() ? motivo : "No especificado");
        solicitud.setEstadoPeticion(EstadoPeticion.Rechazada);
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);

        // Notificar al usuario con template Thymeleaf (capturamos cualquier error de
        // email/template)
        UsuarioEntity usuarioSolicitante = saved.getUsuario();
        try {
            Context context = new Context();
            context.setVariable("nombre", usuarioSolicitante.getNombre());
            context.setVariable("idSolicitud", saved.getIdSolicitud());
            context.setVariable("motivoRechazo", saved.getMotivoRechazo());
            context.setVariable("tipoResiduo", saved.getTipoResiduo());
            context.setVariable("cantidad", saved.getCantidad());
            context.setVariable("descripcion", saved.getDescripcion());
            context.setVariable("localidad", saved.getLocalidad());
            context.setVariable("ubicacion", saved.getUbicacion());
            context.setVariable("fechaProgramada",
                    saved.getFechaProgramada() != null ? saved.getFechaProgramada().toString() : "N/A");

            String contenidoHtml = templateEngine.process("email-rechazaSolicitud", context);

            String asunto = "Solicitud de recolección rechazada";
            emailService.enviarCorreo(usuarioSolicitante.getCorreo(), asunto, contenidoHtml);
        } catch (Exception e) {
            logger.error("Error al procesar/enviar el email de rechazo para solicitudId={}", solicitudId, e);
        }

        return entityToDTO(saved);
    }

    @Override
public SolicitudRecoleccionDTO actualizarSolicitud(Long id, SolicitudRecoleccionDTO dto, String correoUsuario) {
    try {
        // 1. Obtener entidades
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        logger.info("Actualizando solicitud {} con estado actual {}", id, solicitud.getEstadoPeticion());

        // 2. Permisos
        boolean esAdmin = usuario.getRol().getNombre().equals("Administrador");
        boolean esDuenio = solicitud.getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esAdmin && !esDuenio) {
            throw new RuntimeException("No tienes permiso");
        }

        // 3. Estados permitidos
        EstadoPeticion estadoActual = solicitud.getEstadoPeticion();
        if (estadoActual != EstadoPeticion.Pendiente && estadoActual != EstadoPeticion.Cancelada) {
            throw new RuntimeException("Solo se puede editar si está pendiente o cancelada");
        }

        // 4. Validación de fecha 
        if (dto.getFechaProgramada() != null) {
            LocalDateTime hoyInicio = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            if (dto.getFechaProgramada().isBefore(hoyInicio)) {
                throw new RuntimeException("La fecha no puede ser anterior a hoy");
            }
        }

        // 5. Actualizar campos
        if (dto.getTipoResiduo() != null) solicitud.setTipoResiduo(dto.getTipoResiduo());
        if (dto.getCantidad() != null) solicitud.setCantidad(dto.getCantidad());
        if (dto.getDescripcion() != null) solicitud.setDescripcion(dto.getDescripcion());
        if (dto.getLocalidad() != null) solicitud.setLocalidad(dto.getLocalidad());

        // 6. Geocodificación si cambia ubicación
        if (dto.getUbicacion() != null && !dto.getUbicacion().isBlank()) {
            solicitud.setUbicacion(dto.getUbicacion());
            try {
                var coords = geocodingService.obtenerCoordenadas(dto.getUbicacion())
                        .orElseThrow(() -> new RuntimeException("No se pudieron obtener coordenadas"));
                solicitud.setLatitude(BigDecimal.valueOf(coords.latitud()));
                solicitud.setLongitude(BigDecimal.valueOf(coords.longitud()));
            } catch (Exception e) {
                logger.error("Error en geocodificación para ubicación {}: {}", dto.getUbicacion(), e.getMessage());
                throw new RuntimeException("Error al geocodificar la dirección: " + e.getMessage());
            }
        }

        // 7. Actualizar fecha programada
        if (dto.getFechaProgramada() != null) {
            solicitud.setFechaProgramada(dto.getFechaProgramada());
        }

        // 8. Si esta rechazada, la pasamos a pendiente
        if (estadoActual == EstadoPeticion.Cancelada) {
            solicitud.setEstadoPeticion(EstadoPeticion.Pendiente);
            solicitud.setMotivoRechazo(null); 
        }

        // 9. Guardar
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);
        return entityToDTO(saved);

    } catch (Exception e) {
        logger.error("Error actualizando solicitud {}: {}", id, e.getMessage(), e);
        throw new RuntimeException("Error interno al actualizar la solicitud: " + e.getMessage(), e);
    }
}



@Override
    public SolicitudRecoleccionDTO cancelarSolicitud(Long solicitudId) {

        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!solicitud.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No tienes permiso");
        }

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden cancelar solicitudes pendientes");
        }

        solicitud.setEstadoPeticion(EstadoPeticion.Cancelada);

        return entityToDTO(solicitudRepository.save(solicitud));
    }

    // ==========================================================
    // Método auxiliar para filtrar solicitudes
    // ==========================================================
    private List<SolicitudRecoleccionDTO> obtenerSolicitudesFiltradas(EstadoPeticion estado, Localidad localidad,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // 1. Consultar según filtros
        List<SolicitudRecoleccionEntity> entities;
        if (estado != null && localidad != null) {
            entities = solicitudRepository.findByLocalidadAndEstadoPeticion(localidad, estado);
        } else if (estado != null) {
            entities = solicitudRepository.findByEstadoPeticion(estado);
        } else if (localidad != null) {
            entities = solicitudRepository.findByLocalidad(localidad);
        } else {
            entities = solicitudRepository.findAll();
        }

        // 2. Convertir a DTO
        List<SolicitudRecoleccionDTO> dtos = entities.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        return dtos.stream().filter(dto -> {
            LocalDateTime fecha = dto.getFechaProgramada();
            if (fecha == null)
                return false;

            return true;
        }).collect(Collectors.toList());
    }

    // ==========================================================
    // Listar por Usuario y Estado
    // ==========================================================
    @Override
    public List<SolicitudRecoleccionDTO> listarPorUsuario(Long usuarioId) {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRepository.findByUsuario_IdUsuario(usuarioId);

        return solicitudes.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // Listar por Usuario y Estado
    // ==========================================================
    @Override
    public List<SolicitudRecoleccionDTO> listarPorUsuarioYEstado(Long usuarioId, EstadoPeticion estado) {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRepository
                .findByUsuario_IdUsuarioAndEstadoPeticion(usuarioId, estado);

        return solicitudes.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
public void generarReporteExcel(EstadoPeticion estado, Localidad localidad,
                                LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                OutputStream os) throws IOException {
    try {
        List<SolicitudRecoleccionEntity> all = solicitudRepository.findAll();
        List<SolicitudRecoleccionEntity> filtradas = all.stream()
                .filter(e -> estado == null || e.getEstadoPeticion() == estado)
                .filter(e -> localidad == null || e.getLocalidad() == localidad)
                .filter(e -> fechaInicio == null || (e.getFechaProgramada() != null && !e.getFechaProgramada().isBefore(fechaInicio)))
                .filter(e -> fechaFin == null || (e.getFechaProgramada() != null && !e.getFechaProgramada().isAfter(fechaFin)))
                .collect(Collectors.toList());

        List<SolicitudRecoleccionDTO> dtos = filtradas.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        // --- mismo código de creación de Excel que arriba ---
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes");

        String[] headers = {
                "ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad",
                "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion", "Evidencia",
                "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId", "Latitude", "Longitude"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (SolicitudRecoleccionDTO s : dtos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(Optional.ofNullable(s.getIdSolicitud()).map(String::valueOf).orElse(""));
            row.createCell(1).setCellValue(Optional.ofNullable(s.getUsuarioId()).orElse(0L).doubleValue());
            row.createCell(2).setCellValue(Optional.ofNullable(s.getAceptadaPorId()).orElse(0L).doubleValue());
            row.createCell(3).setCellValue(Optional.ofNullable(s.getTipoResiduo()).map(Enum::name).orElse(""));
            row.createCell(4).setCellValue(Optional.ofNullable(s.getCantidad()).orElse(""));
            row.createCell(5).setCellValue(Optional.ofNullable(s.getEstadoPeticion()).map(Enum::name).orElse(""));
            row.createCell(6).setCellValue(Optional.ofNullable(s.getDescripcion()).orElse(""));
            row.createCell(7).setCellValue(Optional.ofNullable(s.getLocalidad()).map(Enum::name).orElse(""));
            row.createCell(8).setCellValue(Optional.ofNullable(s.getUbicacion()).orElse(""));
            row.createCell(9).setCellValue(Optional.ofNullable(s.getEvidencia()).orElse(""));
            row.createCell(10).setCellValue(Optional.ofNullable(s.getFechaCreacionSolicitud()).map(OffsetDateTime::toString).orElse(""));
            row.createCell(11).setCellValue(Optional.ofNullable(s.getFechaProgramada()).map(LocalDateTime::toString).orElse(""));
            row.createCell(12).setCellValue(Optional.ofNullable(s.getRecoleccionId()).orElse(0L).doubleValue());
            row.createCell(13).setCellValue(Optional.ofNullable(s.getLatitude()).map(BigDecimal::doubleValue).orElse(0.0));
            row.createCell(14).setCellValue(Optional.ofNullable(s.getLongitude()).map(BigDecimal::doubleValue).orElse(0.0));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    } catch (Exception e) {
        logger.error("Error generando reporte Excel", e);
        throw new IOException("Error al generar el reporte Excel: " + e.getMessage(), e);
    }
}

@Override
public void generarReportePDF(EstadoPeticion estado, Localidad localidad,
                              LocalDateTime fechaInicio, LocalDateTime fechaFin,
                              OutputStream os) throws IOException, DocumentException {
    try {
        List<SolicitudRecoleccionEntity> all = solicitudRepository.findAll();
        List<SolicitudRecoleccionEntity> filtradas = all.stream()
                .filter(e -> estado == null || e.getEstadoPeticion() == estado)
                .filter(e -> localidad == null || e.getLocalidad() == localidad)
                .filter(e -> fechaInicio == null || (e.getFechaProgramada() != null && !e.getFechaProgramada().isBefore(fechaInicio)))
                .filter(e -> fechaFin == null || (e.getFechaProgramada() != null && !e.getFechaProgramada().isAfter(fechaFin)))
                .collect(Collectors.toList());

        List<SolicitudRecoleccionDTO> dtos = filtradas.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        // --- mismo código de PDF que arriba ---
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph("Reporte de Solicitudes de Recolección"));
        document.add(new Paragraph(" "));

        String[] headers = {
                "ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad",
                "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion",
                "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId"
        };
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (SolicitudRecoleccionDTO s : dtos) {
            table.addCell(Optional.ofNullable(s.getIdSolicitud()).map(Object::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getUsuarioId()).map(Object::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getAceptadaPorId()).map(Object::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getTipoResiduo()).map(Enum::name).orElse(""));
            table.addCell(Optional.ofNullable(s.getCantidad()).orElse(""));
            table.addCell(Optional.ofNullable(s.getEstadoPeticion()).map(Enum::name).orElse(""));
            table.addCell(Optional.ofNullable(s.getDescripcion()).orElse(""));
            table.addCell(Optional.ofNullable(s.getLocalidad()).map(Enum::name).orElse(""));
            table.addCell(Optional.ofNullable(s.getUbicacion()).orElse(""));
            table.addCell(Optional.ofNullable(s.getFechaCreacionSolicitud()).map(OffsetDateTime::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getFechaProgramada()).map(LocalDateTime::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getRecoleccionId()).map(Object::toString).orElse(""));
        }

        document.add(table);
        document.close();
    } catch (Exception e) {
        logger.error("Error generando reporte PDF", e);
        throw new IOException("Error al generar el reporte PDF: " + e.getMessage(), e);
    }
}

}
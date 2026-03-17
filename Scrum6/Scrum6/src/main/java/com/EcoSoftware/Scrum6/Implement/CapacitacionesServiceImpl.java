package com.EcoSoftware.Scrum6.Implement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.CapacitacionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.InscripcionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ModuloDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ProgresoDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.UploadResultDTO;
import com.EcoSoftware.Scrum6.Entity.CapacitacionEntity;
import com.EcoSoftware.Scrum6.Entity.InscripcionEntity;
import com.EcoSoftware.Scrum6.Entity.ModuloEntity;
import com.EcoSoftware.Scrum6.Entity.ProgresoEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;
import com.EcoSoftware.Scrum6.Exception.ValidacionCapacitacionException;
import com.EcoSoftware.Scrum6.Repository.CapacitacionRepository;
import com.EcoSoftware.Scrum6.Repository.InscripcionRepository;
import com.EcoSoftware.Scrum6.Repository.ModuloRepository;
import com.EcoSoftware.Scrum6.Repository.ProgresoRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.CapacitacionesService;
import com.EcoSoftware.Scrum6.Service.CloudinaryService;

@Service
@Transactional
public class CapacitacionesServiceImpl implements CapacitacionesService {

    @Autowired
    private CapacitacionRepository capacitacionRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ProgresoRepository progresoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private com.EcoSoftware.Scrum6.Service.EmailService emailService;

    @Autowired
    private org.thymeleaf.TemplateEngine templateEngine;

    @Autowired
    private CloudinaryService cloudinaryService;

    /** Verificar si la capacitacion existe por nombre  o descripcion*/
    @Override
    public boolean existeCapacitacionPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        return capacitacionRepository.existsByNombreIgnoreCase(nombre.trim());
    }

    @Override
    public boolean existeCapacitacionPorDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) return false;
        return capacitacionRepository.existsByDescripcionIgnoreCase(descripcion.trim());
    }

    // ============================
    // UTIL: distancia de Levenshtein / similitud
    // ============================
    private int calcularDistanciaLevenshtein(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        a = a.toLowerCase();
        b = b.toLowerCase();

        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    private boolean esSimilar(String a, String b) {
        if (a == null || b == null) return false;
        int distancia = calcularDistanciaLevenshtein(a, b);
        int longitud = Math.max(a.length(), b.length());
        if (longitud == 0) return true;
        double similarity = 1.0 - ((double) distancia / longitud);
        // Umbral configurable: 0.70 => 70% de similaridad o más
        return similarity >= 0.70;
    }

    // ============================
    // VALIDAR CAPACITACIONES DESDE EXCEL (retorna lista con observaciones)
    // ============================
    @Override
    public List<CapacitacionDTO> validarCapacitacionesExcel(MultipartFile file) {
        List<CapacitacionDTO> resultado = new ArrayList<>();
        List<CapacitacionEntity> existentes = capacitacionRepository.findAll();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar encabezado

                String nombre = getCellValue(row.getCell(0));
                String descripcion = getCellValue(row.getCell(1));

                // Ignorar filas sin nombre
                if (nombre == null || nombre.isBlank()) continue;

                boolean nombreExacto = existentes.stream()
                        .anyMatch(c -> c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre));

                boolean descripcionExacta = existentes.stream()
                        .anyMatch(c -> c.getDescripcion() != null && c.getDescripcion().equalsIgnoreCase(descripcion));

                boolean nombreSimilar = existentes.stream()
                        .anyMatch(c -> c.getNombre() != null && esSimilar(c.getNombre(), nombre));

                // Construir DTO con observación
                CapacitacionDTO dto = new CapacitacionDTO();
                dto.setNombre(nombre);
                dto.setDescripcion(descripcion);

                if (nombreExacto) {
                    dto.setObservacion("ERROR: nombre repetido");
                    resultado.add(dto);
                    // Si es error por nombre exacto, no necesitamos añadir más observaciones para esta fila
                    continue;
                }

                // Si descripción exacta -> warning
                if (descripcionExacta) {
                    dto.setObservacion("WARNING: descripción repetida");
                    resultado.add(dto);
                } else if (nombreSimilar) {
                    dto.setObservacion("WARNING: nombre parecido a existente");
                    resultado.add(dto);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al validar el archivo Excel: " + e.getMessage(), e);
        }

        return resultado;
    }

    /**
     * Carga masiva de capacitaciones desde un archivo Excel.
     * Ahora devuelve un UploadResultDTO con detalles.
     * Solo crea nuevas capacitaciones (no actualiza existentes).
     */
    @Override
    public UploadResultDTO cargarCapacitacionesDesdeExcel(MultipartFile file) {
        List<CapacitacionDTO> validaciones = validarCapacitacionesExcel(file);

        // Separar errores bloqueantes y warnings
        List<CapacitacionDTO> errores = validaciones.stream()
                .filter(d -> d.getObservacion() != null && d.getObservacion().startsWith("ERROR"))
                .collect(Collectors.toList());

        List<CapacitacionDTO> avisos = validaciones.stream()
                .filter(d -> d.getObservacion() != null && d.getObservacion().startsWith("WARNING"))
                .collect(Collectors.toList());

        if (!errores.isEmpty()) {
            // Construir mensaje con los nombres repetidos
            String nombres = errores.stream()
                    .map(CapacitacionDTO::getNombre)
                    .collect(Collectors.toList())
                    .toString();

            // Lanzar excepción con la lista de duplicadas
            throw new ValidacionCapacitacionException(
                    "Se detectaron capacitaciones repetidas por nombre: " + nombres,
                    errores
            );
        }

        UploadResultDTO result = new UploadResultDTO();
        int totalLeidas = 0;
        int insertadas = 0;
        int rechazadas = 0;

        List<CapacitacionEntity> paraGuardar = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // saltar encabezado

            while (rows.hasNext()) {
                Row row = rows.next();
                totalLeidas++;

                String nombre = getCellValue(row.getCell(0));
                String descripcion = getCellValue(row.getCell(1));
                String numeroDeClases = getCellValue(row.getCell(2));
                String duracion = getCellValue(row.getCell(3));
                String imagen = getCellValue(row.getCell(4));

                // Validación básica: nombre obligatorio
                if (nombre == null || nombre.isBlank()) {
                    rechazadas++;
                    continue;
                }

                // Verificación final por nombre (evita TOCTOU)
                if (capacitacionRepository.existsByNombreIgnoreCase(nombre)) {
                    CapacitacionDTO err = new CapacitacionDTO();
                    err.setNombre(nombre);
                    err.setDescripcion(descripcion);
                    err.setObservacion("ERROR: nombre repetido (existente en BD en el momento de la carga)");
                    List<CapacitacionDTO> listaErr = new ArrayList<>();
                    listaErr.add(err);
                    throw new ValidacionCapacitacionException(
                            "Nombre repetido detectado durante la carga: [" + nombre + "]",
                            listaErr
                    );
                }

                CapacitacionEntity c = new CapacitacionEntity();
                c.setNombre(nombre);
                c.setDescripcion(descripcion);
                c.setNumeroDeClases(numeroDeClases);
                c.setDuracion(duracion);
                c.setImagen(imagen);

                paraGuardar.add(c);
                insertadas++;
            }

            // Guardar todas las nuevas en batch
            if (!paraGuardar.isEmpty()) {
                capacitacionRepository.saveAll(paraGuardar);
            }

            result.setTotalFilasLeidas(totalLeidas);
            result.setInsertadas(insertadas);
            result.setRechazadas(rechazadas);
            result.setWarnings(avisos.size());
            result.setErrores(new ArrayList<>());
            result.setAvisos(avisos);
            result.setMensaje("Carga finalizada correctamente.");

            return result;

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo Excel", e);
        }
    }

    /**
     * Genera una plantilla Excel con los campos requeridos para la carga masiva.
     * Devuelve un arreglo de bytes que se puede descargar desde el frontend.
     */
    @Override
    public byte[] generarPlantillaExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plantilla Capacitaciones");
            Row header = sheet.createRow(0);

            // Encabezados
            header.createCell(0).setCellValue("Nombre");
            header.createCell(1).setCellValue("Descripción");
            header.createCell(2).setCellValue("Número de Clases");
            header.createCell(3).setCellValue("Duración");
            header.createCell(4).setCellValue("Imagen");

            // Ajustar tamaño de columnas
            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar la plantilla Excel", e);
        }
    }

    /**
     *
     * Método auxiliar para leer valores de celda como String.
     */
    private String getCellValue(Cell cell) {
        if (cell == null)
            return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d)) {
                    return String.valueOf((long) d);
                } else {
                    return String.valueOf(d);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception ex) {
                    try {
                        double dv = cell.getNumericCellValue();
                        if (dv == Math.floor(dv)) return String.valueOf((long) dv);
                        return String.valueOf(dv);
                    } catch (Exception e) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }

    // ============================
    // CRUD Capacitacion (sin cambios lógicos, mapeos simples)
    // ============================
    @Override
    public CapacitacionDTO crearCapacitacion(CapacitacionDTO dto) {
        CapacitacionEntity entidad = new CapacitacionEntity();
        entidad.setNombre(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setNumeroDeClases(dto.getNumeroDeClases());
        entidad.setDuracion(dto.getDuracion());
        entidad.setImagen(dto.getImagen());
        CapacitacionEntity saved = capacitacionRepository.save(entidad);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
public String subirImagen(MultipartFile file, Long capacitacionId) throws Exception {

    CapacitacionEntity cap = capacitacionRepository.findById(capacitacionId)
            .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

    if (file == null || file.isEmpty()) {
        throw new RuntimeException("Archivo no enviado");
    }

    if (!file.getContentType().startsWith("image/")) {
        throw new RuntimeException("Archivo no es una imagen válida");
    }

    String folder = "capacitaciones/" + capacitacionId;

    
    String publicId = "imagen_principal";

    String url = cloudinaryService.upload(file, folder, publicId);

    cap.setImagen(url);
    capacitacionRepository.save(cap);

    return url;
}

    @Override
    public CapacitacionDTO actualizarCapacitacion(Long id, CapacitacionDTO dto) {
        CapacitacionEntity entidad = capacitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));
        entidad.setNombre(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setNumeroDeClases(dto.getNumeroDeClases());
        entidad.setDuracion(dto.getDuracion());
        entidad.setImagen(dto.getImagen());
        capacitacionRepository.save(entidad);
        dto.setId(entidad.getId());
        return dto;
    }

    @Override
    public void eliminarCapacitacion(Long id) {
        capacitacionRepository.deleteById(id);
    }

    @Override
    public CapacitacionDTO obtenerCapacitacionPorId(Long id) {
        CapacitacionEntity entidad = capacitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));
        CapacitacionDTO dto = new CapacitacionDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setNumeroDeClases(entidad.getNumeroDeClases());
        dto.setDuracion(entidad.getDuracion());
        dto.setImagen(entidad.getImagen());
        return dto;
    }

    @Override
    public List<CapacitacionDTO> listarTodasCapacitaciones() {
        return capacitacionRepository.findAll().stream().map(entidad -> {
            CapacitacionDTO dto = new CapacitacionDTO();
            dto.setId(entidad.getId());
            dto.setNombre(entidad.getNombre());
            dto.setDescripcion(entidad.getDescripcion());
            dto.setNumeroDeClases(entidad.getNumeroDeClases());
            dto.setDuracion(entidad.getDuracion());
            dto.setImagen(entidad.getImagen());
            return dto;
        }).collect(Collectors.toList());
    }

@Override
public List<CapacitacionDTO> obtenerCapacitacionesUsuario(Long usuarioId) {

    return inscripcionRepository
            .findByUsuario_IdUsuario(usuarioId)
            .stream()
            .map(ins -> ins.getCurso())
            .filter(curso -> curso != null) // 👈 evita null
            .map(curso -> {
                CapacitacionDTO dto = new CapacitacionDTO();
                dto.setId(curso.getId());
                dto.setNombre(curso.getNombre());
                dto.setDescripcion(curso.getDescripcion());
                dto.setNumeroDeClases(curso.getNumeroDeClases());
                dto.setDuracion(curso.getDuracion());
                dto.setImagen(curso.getImagen());
                return dto;
            })
            .collect(Collectors.toList());
}

    // ============================
    // Módulos (sin cambios funcionales)
    // ============================
    @Override
    public ModuloDTO crearModulo(ModuloDTO dto) {
        ModuloEntity entidad = new ModuloEntity();
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setDuracion(dto.getDuracion());

        CapacitacionEntity curso = capacitacionRepository.findById(dto.getCapacitacionId())
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));
        entidad.setCapacitacion(curso);

        ModuloEntity saved = moduloRepository.save(entidad);
        dto.setId(saved.getId());
        return dto;
    }

    

    @Override
    public ModuloDTO actualizarModulo(Long id, ModuloDTO dto) {
        ModuloEntity entidad = moduloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setDuracion(dto.getDuracion());
        moduloRepository.save(entidad);
        dto.setId(entidad.getId());
        return dto;
    }

    @Override
    public void eliminarModulo(Long id) {
        moduloRepository.deleteById(id);
    }

    @Override
    public List<ModuloDTO> listarModulosPorCapacitacion(Long capacitacionId) {
        return moduloRepository.findByCapacitacionId(capacitacionId).stream().map(entidad -> {
            ModuloDTO dto = new ModuloDTO();
            dto.setId(entidad.getId());
            dto.setDescripcion(entidad.getDescripcion());
            dto.setDuracion(entidad.getDuracion());
            dto.setCapacitacionId(entidad.getCapacitacion().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] generarPlantillaModulosExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plantilla Módulos");
            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Duración");
            header.createCell(1).setCellValue("Descripción");

            for (int i = 0; i <= 1; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar la plantilla Excel de módulos", e);
        }
    }

    @Override
    public void cargarModulosDesdeExcel(Long capacitacionId, MultipartFile file) {
        CapacitacionEntity capacitacion = capacitacionRepository.findById(capacitacionId)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ModuloEntity> modulos = new ArrayList<>();

            if (rows.hasNext())
                rows.next(); // Saltar encabezado

            while (rows.hasNext()) {
                Row row = rows.next();

                String duracion = getCellValue(row.getCell(0));
                String descripcion = getCellValue(row.getCell(1));

                if ((duracion == null || duracion.isBlank()) &&
                        (descripcion == null || descripcion.isBlank()))
                    continue;

                ModuloEntity m = new ModuloEntity();
                m.setDuracion(duracion);
                m.setDescripcion(descripcion);
                m.setCapacitacion(capacitacion);

                modulos.add(m);
            }

            moduloRepository.saveAll(modulos);

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo Excel de módulos", e);
        }
    }

    // ============================
    // Inscripciones y Progreso (sin cambios)
    // ============================
    @Override
    public InscripcionDTO inscribirse(Long usuarioId, Long cursoId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CapacitacionEntity curso = capacitacionRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

        InscripcionEntity entidad = new InscripcionEntity();
        entidad.setUsuario(usuario);
        entidad.setCurso(curso);
        entidad.setFechaDeInscripcion(java.time.LocalDate.now());
        entidad.setEstadoCurso(EstadoCurso.Inscrito);

        InscripcionEntity saved = inscripcionRepository.save(entidad);

        InscripcionDTO dto = new InscripcionDTO();
        dto.setId(saved.getId());
        dto.setCursoId(curso.getId());
        dto.setUsuarioId(usuario.getIdUsuario());
        dto.setEstadoCurso(saved.getEstadoCurso());
        dto.setFechaDeInscripcion(saved.getFechaDeInscripcion());
        // Enviar correo de confirmación al usuario
        try {
            org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
            ctx.setVariable("nombreUsuario", usuario.getNombre());
            ctx.setVariable("nombreCurso", curso.getNombre());
            ctx.setVariable("fechaInscripcion", saved.getFechaDeInscripcion().toString());

            String contenido = templateEngine.process("email-inscripcion", ctx);
            String asunto = "Confirmación de inscripción: " + curso.getNombre();
            emailService.enviarCorreo(usuario.getCorreo(), asunto, contenido);
        } catch (Exception e) {
            System.err.println("Error enviando correo de inscripción: " + e.getMessage());
        }

        return dto;
    }

    @Override
    public InscripcionDTO actualizarEstadoInscripcion(Long id, EstadoCurso estadoCurso) {
        InscripcionEntity entidad = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        entidad.setEstadoCurso(estadoCurso);
        inscripcionRepository.save(entidad);

        InscripcionDTO dto = new InscripcionDTO();
        dto.setId(entidad.getId());
        dto.setCursoId(entidad.getCurso().getId());
        dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
        dto.setEstadoCurso(entidad.getEstadoCurso());
        dto.setFechaDeInscripcion(entidad.getFechaDeInscripcion());

        // Enviar notificación al usuario sobre el cambio de estado
        try {
            UsuarioEntity usuario = entidad.getUsuario();
            CapacitacionEntity curso = entidad.getCurso();
            org.thymeleaf.context.Context ctx = new org.thymeleaf.context.Context();
            ctx.setVariable("nombreUsuario", usuario.getNombre());
            ctx.setVariable("nombreCurso", curso.getNombre());
            ctx.setVariable("nuevoEstado", entidad.getEstadoCurso().name());

            String contenido = templateEngine.process("email-estadoInscripcion", ctx);
            String asunto = "Actualización de su inscripción: " + curso.getNombre();
            emailService.enviarCorreo(usuario.getCorreo(), asunto, contenido);
        } catch (Exception e) {
            System.err.println("Error enviando correo de actualización de inscripción: " + e.getMessage());
        }

        return dto;
    }

    @Override
    public java.util.List<InscripcionDTO> listarInscripcionesPorUsuario(Long usuarioId) {
        return inscripcionRepository.findByUsuario_IdUsuario(usuarioId).stream().map(entidad -> {
            InscripcionDTO dto = new InscripcionDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setEstadoCurso(entidad.getEstadoCurso());
            dto.setFechaDeInscripcion(entidad.getFechaDeInscripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public java.util.List<InscripcionDTO> listarInscripcionesPorCurso(Long cursoId) {
        return inscripcionRepository.findByCursoId(cursoId).stream().map(entidad -> {
            InscripcionDTO dto = new InscripcionDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setEstadoCurso(entidad.getEstadoCurso());
            dto.setFechaDeInscripcion(entidad.getFechaDeInscripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ProgresoDTO registrarProgreso(ProgresoDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CapacitacionEntity curso = capacitacionRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

        ProgresoEntity entidad = new ProgresoEntity();
        entidad.setUsuario(usuario);
        entidad.setCurso(curso);
        entidad.setProgresoDelCurso(dto.getProgresoDelCurso());
        entidad.setModulosCompletados(dto.getModulosCompletados());
        entidad.setTiempoInvertido(dto.getTiempoInvertido());

        ProgresoEntity saved = progresoRepository.save(entidad);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public ProgresoDTO actualizarProgreso(Long id, ProgresoDTO dto) {
        ProgresoEntity entidad = progresoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado"));
        entidad.setProgresoDelCurso(dto.getProgresoDelCurso());
        entidad.setModulosCompletados(dto.getModulosCompletados());
        entidad.setTiempoInvertido(dto.getTiempoInvertido());
        progresoRepository.save(entidad);
        dto.setId(entidad.getId());
        return dto;
    }

    @Override
    public java.util.List<ProgresoDTO> listarProgresosPorUsuario(Long usuarioId) {
        return progresoRepository.findByUsuario_IdUsuario(usuarioId).stream().map(entidad -> {
            ProgresoDTO dto = new ProgresoDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setProgresoDelCurso(entidad.getProgresoDelCurso());
            dto.setModulosCompletados(entidad.getModulosCompletados());
            dto.setTiempoInvertido(entidad.getTiempoInvertido());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public java.util.List<ProgresoDTO> listarProgresosPorCurso(Long cursoId) {
        return progresoRepository.findByCursoId(cursoId).stream().map(entidad -> {
            ProgresoDTO dto = new ProgresoDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setProgresoDelCurso(entidad.getProgresoDelCurso());
            dto.setModulosCompletados(entidad.getModulosCompletados());
            dto.setTiempoInvertido(entidad.getTiempoInvertido());
            return dto;
        }).collect(Collectors.toList ());
    }
}

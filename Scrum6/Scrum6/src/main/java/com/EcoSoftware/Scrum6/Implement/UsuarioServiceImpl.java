package com.EcoSoftware.Scrum6.Implement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.CloudinaryService;
import com.EcoSoftware.Scrum6.Service.UsuarioService;
import com.EcoSoftware.Scrum6.Util.PasswordPolicyUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.EcoSoftware.Scrum6.Service.EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAllByOrderByIdUsuarioAsc()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + idUsuario));
        return convertirADTO(usuarioEntity);
    }

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getRolId() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }

        PasswordPolicyUtil.validar(usuarioDTO.getContrasena());

        UsuarioEntity entity = modelMapper.map(usuarioDTO, UsuarioEntity.class);

        // Buscar el rol
        RolEntity rol = rolRepository.findById(usuarioDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + usuarioDTO.getRolId()));
        entity.setRol(rol);

        // Cifrar contraseña
        entity.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));

        // DEFINIR ESTADO SEGÚN ROL
        Long rolId = usuarioDTO.getRolId();
        if (rolId == 1 || rolId == 2) {
            entity.setEstadoRegistro(EstadoRegistro.APROBADO); // Ciudadano y Administrador se aprueban automáticamente
        } else {
            entity.setEstadoRegistro(EstadoRegistro.PENDIENTE_DOCUMENTACION); // Reciclador y Empresa deben subir
                                                                              // documentos y ser revisados por admin
                                                                              // antes de aprobar
        }

        // Fechas
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setFechaActualizacion(LocalDateTime.now());

        // Guardar usuario
        UsuarioEntity saved = usuarioRepository.save(entity);

        // Enviar correo
        Context context = new Context();
        context.setVariable("nombre", saved.getNombre());
        String html = templateEngine.process("email-bienvenida", context);

        emailService.enviarCorreo(saved.getCorreo(),
                "¡Bienvenido a EcoSoftware!", html);

        // Convertir a DTO (sin contraseña)
        UsuarioDTO result = modelMapper.map(saved, UsuarioDTO.class);
        result.setContrasena(null);

        return result;
    }

    // aprobación de regsitro
    @Override
    public void aprobarUsuario(Long idUsuario) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEstadoRegistro() == EstadoRegistro.APROBADO) {
            throw new RuntimeException("El usuario ya está aprobado");
        }

        usuario.setEstadoRegistro(EstadoRegistro.APROBADO);
        usuario.setFechaActualizacion(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    // elimición de registro
    @Override
    public void rechazarUsuario(Long idUsuario) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstadoRegistro(EstadoRegistro.RECHAZADO);
        usuario.setFechaActualizacion(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioDTO> listarUsuariosPendientes() {
        return usuarioRepository
                .findByEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public Long contarUsuariosPendientes() {
        return usuarioRepository.countByEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR);
    }


    @Override
public String subirDocumento(MultipartFile file, Long idUsuario, String tipo) throws IOException {

    if (file == null || file.isEmpty()) {
        throw new RuntimeException("Archivo no enviado");
    }

    UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    String folder = "usuarios/" + idUsuario;
    String publicId = tipo + "_" + System.currentTimeMillis();

    String url = cloudinaryService.upload(file, folder, publicId);

    switch (tipo.toUpperCase()) {

        case "CEDULA":
            usuario.setDocumento(url);
            break;

        case "CERTIFICADO":
            usuario.setCertificaciones(url);
            break;

        case "RUT":
            usuario.setRut(url);
            break;

        case "CAMARA":
            usuario.setCamara_comercio(url);
            break;

        case "FOTO_PERFIL":
            usuario.setImagen_perfil(url);
            break;

        default:
            throw new RuntimeException("Tipo de documento no válido");
    }

    validarEstadoDocumentacion(usuario);

    usuario.setFechaActualizacion(LocalDateTime.now());
    usuarioRepository.save(usuario);

    return url;
}

private void validarEstadoDocumentacion(UsuarioEntity usuario) {

    String rol = usuario.getRol().getTipo().name();

    boolean documentosCompletos = false;

    if (rol.equals("Empresa")) {

        documentosCompletos =
                usuario.getDocumento() != null &&
                usuario.getRut() != null &&
                usuario.getCamara_comercio() != null;

    } else if (rol.equals("Reciclador")) {

        documentosCompletos =
                usuario.getDocumento() != null;
    }

    if (documentosCompletos) {
        usuario.setEstadoRegistro(EstadoRegistro.PENDIENTE_REVISAR);
    } else {
        usuario.setEstadoRegistro(EstadoRegistro.PENDIENTE_DOCUMENTACION);
    }
}

   
    // ========================================================
    // GENERAR PLANTILLA POR ROL
    // ========================================================
    @Override
    public byte[] generarPlantillaExcelPorRol(String rol) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Plantilla");

        // Campos base obligatorios
        List<String> columnas = new ArrayList<>(List.of(
                "Nombre",
                "Correo",
                "Contrasena",
                "Cedula",
                "Telefono",
                "Direccion",
                "Localidad"));

        switch (rol) {
            case "Reciclador":
                columnas.add("ZonaTrabajo");
                break;

            case "Empresa":
                columnas.add("Nit");
                columnas.add("RepresentanteLegal");
                break;

            case "Ciudadano":
            case "Administrador":
                // Solo campos base
                break;

            default:
                throw new RuntimeException("Rol no valido para plantilla");
        }

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.size(); i++) {
            headerRow.createCell(i).setCellValue(columnas.get(i));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Error generando plantilla", e);
        }

        return out.toByteArray();
    }

    // ========================================================
    // CARGAR ARCHIVO EXCEL POR ROL
    // ========================================================
    @Override
    public List<String> cargarUsuariosDesdeExcel(String rol, MultipartFile file) {

        List<String> errores = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    UsuarioEntity usuario = new UsuarioEntity();

                    // CAMPOS BASE
                    usuario.setNombre(getCellValueAsString(row, 0));
                    usuario.setCorreo(getCellValueAsString(row, 1));
                    usuario.setContrasena(getCellValueAsString(row, 2));
                    usuario.setCedula(getCellValueAsString(row, 3));
                    usuario.setTelefono(getCellValueAsString(row, 4));
                    usuario.setDireccion(getCellValueAsString(row, 5));
                    usuario.setLocalidad(getCellValueAsString(row, 6));

                    // CAMPOS SEGÚN ROL
                    switch (rol.toUpperCase()) {
                        case "RECICLADOR":
                            usuario.setZona_de_trabajo(getCellValueAsString(row, 7));
                            break;

                        case "EMPRESA":
                            usuario.setNit(getCellValueAsString(row, 7));
                            usuario.setRepresentanteLegal(getCellValueAsString(row, 8));
                            break;

                        // Ciudadano y Administrador solo usan campos base
                    }

                    // Buscar rol
                    RolEntity rolEntity = rolRepository.findByNombreIgnoreCase(rol)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    usuario.setRol(rolEntity);

                    // Guardar usuario
                    usuarioRepository.save(usuario);

                } catch (Exception e) {
                    errores.add("Fila " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error procesando archivo Excel", e);
        }

        return errores;
    }

    // Método auxiliar para leer celdas como String
    private String getCellValueAsString(Row row, int index) {
        if (row.getCell(index) == null)
            return "";

        switch (row.getCell(index).getCellType()) {
            case STRING:
                return row.getCell(index).getStringCellValue();
            case NUMERIC:
                // Convertimos a long si no hay decimales
                double val = row.getCell(index).getNumericCellValue();
                if (val == Math.floor(val)) {
                    return String.valueOf((long) val);
                } else {
                    return String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(row.getCell(index).getBooleanCellValue());
            case FORMULA:
                return row.getCell(index).getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    @Override
    public UsuarioEditarDTO actualizarUsuario(Long idUsuario, UsuarioEditarDTO usuarioDTO) {
        UsuarioEntity usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        String rol = usuarioExistente.getRol().getNombre();
        String zonaOriginal = usuarioExistente.getZona_de_trabajo();
        Integer cantidadMinima = usuarioExistente.getCantidad_minima();
        modelMapper.map(usuarioDTO, usuarioExistente);

        if (rol.equals("Ciudadano")) {
            usuarioExistente.setZona_de_trabajo(zonaOriginal);
            usuarioExistente.setCantidad_minima(cantidadMinima);
        }

        UsuarioEntity usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return convertirAEditarUsuarioDTO(usuarioActualizado);
    }

    @Override
    public void eliminarPersona(Long idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        usuarioRepository.delete(usuarioEntity);
    }

    @Override
    public void eliminacionPorEstado(Long idUsuario) {
        int filasActualizadas = usuarioRepository.eliminacionLogica(idUsuario);
        if (filasActualizadas == 0) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
    }

    @Override
    public List<UsuarioDTO> encontrarPorNombre(String nombre) {
        Optional<UsuarioEntity> reExactos = usuarioRepository.findByNombreAndEstadoTrue(nombre);
        if (!reExactos.isEmpty()) {
            return reExactos.stream().map(this::convertirADTO).toList();
        } else {
            List<UsuarioEntity> parecidos = usuarioRepository.findByNombreContainingIgnoreCaseAndEstadoTrue(nombre);
            return parecidos.stream().map(this::convertirADTO).toList();
        }
    }

    @Override
    public List<UsuarioDTO> encontrarPorDocumento(String documento) {
        List<UsuarioEntity> usuarios = usuarioRepository.findByCedulaAndEstadoTrue(documento).stream().toList();
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByNitAndEstadoTrue(documento).stream().toList();
        }
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByCedulaContainingIgnoreCaseAndEstadoTrue(documento);
        }
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByNitContainingIgnoreCaseAndEstadoTrue(documento);
        }
        if (usuarios.isEmpty()) {
            throw new RuntimeException("numero de documento no encontrado");
        }
        return usuarios.stream().map(this::convertirADTO).toList();
    }

    @Override
    public List<UsuarioDTO> encontrarPorCorreo(String correo) {
        List<UsuarioEntity> buscarCorreo = usuarioRepository.findByCorreoAndEstadoTrue(correo).stream().toList();
        if (buscarCorreo.isEmpty()) {
            buscarCorreo = usuarioRepository.findByCorreoContainingIgnoreCaseAndEstadoTrue(correo);
        }
        if (buscarCorreo.isEmpty()) {
            throw new RuntimeException("Correo no encontrado");
        }

        return buscarCorreo.stream().map(this::convertirADTO).toList();
    }

    public UsuarioDTO convertirADTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioDTO.class);
    }

    public UsuarioEditarDTO convertirAEditarUsuarioDTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioEditarDTO.class);
    }

    // =====================================================
    // MÉTODO AUXILIAR para filtrar usuarios reutilizable
    // =====================================================
    private List<UsuarioDTO> obtenerUsuariosFiltrados(String nombre, String correo, String documento) {
        if ((nombre == null || nombre.isEmpty()) &&
                (correo == null || correo.isEmpty()) &&
                (documento == null || documento.isEmpty())) {
            return listarUsuarios();
        }
        return usuarioRepository.findByFiltros(nombre, correo, documento)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    // ==============================
    // Exportar usuarios a CSV
    // ==============================

    // ==============================
    // Exportar usuarios a Excel
    // ==============================
    @Override
    public void exportUsuariosToExcel(String nombre, String correo, String documento, OutputStream os)
            throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        String[] headers = { "ID", "Rol ID", "Nombre", "Correo", "Cédula", "Teléfono", "NIT", "Dirección",
                "Barrio", "Localidad", "Zona de Trabajo", "Horario", "Certificaciones",
                "Cantidad Mínima", "Estado", "Fecha Creación" };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<UsuarioDTO> usuarios = obtenerUsuariosFiltrados(nombre, correo, documento);

        int rowNum = 1;
        for (UsuarioDTO usuario : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(usuario.getIdUsuario());
            row.createCell(1).setCellValue(usuario.getRolId() != null ? usuario.getRolId() : 0);
            row.createCell(2).setCellValue(usuario.getNombre());
            row.createCell(3).setCellValue(usuario.getCorreo());
            row.createCell(4).setCellValue(usuario.getCedula());
            row.createCell(5).setCellValue(usuario.getTelefono());
            row.createCell(6).setCellValue(usuario.getNit() != null ? usuario.getNit() : "");
            row.createCell(7).setCellValue(usuario.getDireccion() != null ? usuario.getDireccion() : "");
            row.createCell(8).setCellValue(usuario.getBarrio());
            row.createCell(9).setCellValue(usuario.getLocalidad());
            row.createCell(10).setCellValue(usuario.getZona_de_trabajo() != null ? usuario.getZona_de_trabajo() : "");
            row.createCell(11).setCellValue(usuario.getHorario() != null ? usuario.getHorario() : "");
            row.createCell(12).setCellValue(usuario.getCertificaciones() != null ? usuario.getCertificaciones() : "");
            row.createCell(13).setCellValue(usuario.getCantidad_minima() != null ? usuario.getCantidad_minima() : 0);
            row.createCell(14).setCellValue(usuario.getEstado() != null && usuario.getEstado() ? "Activo" : "Inactivo");
            row.createCell(15)
                    .setCellValue(usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

    // ==============================
    // Exportar usuarios a PDF
    // ==============================
    @Override
    public void exportUsuariosToPDF(String nombre, String correo, String documento, OutputStream os)
            throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph("Reporte de Usuarios"));
        document.add(new Paragraph(" "));

        String[] headers = { "ID", "Rol ID", "Nombre", "Correo", "Cédula", "Teléfono", "NIT", "Dirección",
                "Barrio", "Localidad", "Zona de Trabajo", "Horario", "Certificaciones",
                "Cantidad Mínima", "Estado", "Fecha Creación" };

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        List<UsuarioDTO> usuarios = obtenerUsuariosFiltrados(nombre, correo, documento);

        for (UsuarioDTO usuario : usuarios) {
            table.addCell(usuario.getIdUsuario() != null ? usuario.getIdUsuario().toString() : "");
            table.addCell(usuario.getRolId() != null ? usuario.getRolId().toString() : "");
            table.addCell(usuario.getNombre());
            table.addCell(usuario.getCorreo());
            table.addCell(usuario.getCedula());
            table.addCell(usuario.getTelefono());
            table.addCell(usuario.getNit() != null ? usuario.getNit() : "");
            table.addCell(usuario.getDireccion() != null ? usuario.getDireccion() : "");
            table.addCell(usuario.getBarrio());
            table.addCell(usuario.getLocalidad());
            table.addCell(usuario.getZona_de_trabajo() != null ? usuario.getZona_de_trabajo() : "");
            table.addCell(usuario.getHorario() != null ? usuario.getHorario() : "");
            table.addCell(usuario.getCertificaciones() != null ? usuario.getCertificaciones() : "");
            table.addCell(usuario.getCantidad_minima() != null ? usuario.getCantidad_minima().toString() : "");
            table.addCell(usuario.getEstado() != null && usuario.getEstado() ? "Activo" : "Inactivo");
            table.addCell(usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : "");
        }

        document.add(table);
        document.close();
    }

    @Override
    public Map<String, Map<String, Long>> obtenerUsuariosPorLocalidadYRol() {

        List<UsuarioEntity> usuarios = usuarioRepository.findByEstadoTrue();

        Map<String, Map<String, Long>> resultado = new HashMap<>();

        for (UsuarioEntity u : usuarios) {

            // Filtrar solo roles válidos
            String rol = u.getRol().getNombre();
            if (!rol.equalsIgnoreCase("Ciudadano")
                    && !rol.equalsIgnoreCase("Empresa")
                    && !rol.equalsIgnoreCase("Reciclador")) {
                continue;
            }

            String localidad = u.getLocalidad();
            if (localidad == null)
                continue;

            resultado.putIfAbsent(localidad, new HashMap<>());
            Map<String, Long> conteos = resultado.get(localidad);

            conteos.put(rol, conteos.getOrDefault(rol, 0L) + 1);
        }

        return resultado;
    }

    @Override
    public List<Object[]> obtenerUsuariosPorBarrioYLocalidad() {
        return usuarioRepository.contarUsuariosPorLocalidad();
    }

}

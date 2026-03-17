package com.EcoSoftware.Scrum6.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.itextpdf.text.DocumentException;

public interface UsuarioService {

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO obtenerUsuarioPorId(Long idUsuario);

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    void aprobarUsuario(Long idUsuario);

    void rechazarUsuario(Long idUsuario);

    // Listados y contador para admin
    List<UsuarioDTO> listarUsuariosPendientes(); // devuelve usuarios con estado = false

    Long contarUsuariosPendientes();

    // Subida de documentos 
    String subirDocumento(MultipartFile file, Long idUsuario, String tipo) throws IOException;

    UsuarioEditarDTO actualizarUsuario(Long idUsuario, UsuarioEditarDTO usuDTO);

    void eliminarPersona(Long idUsuario);

    void eliminacionPorEstado(Long idUsuario);

    List<UsuarioDTO> encontrarPorDocumento(String documento);

    List<UsuarioDTO> encontrarPorNombre(String nombre);

    List<UsuarioDTO> encontrarPorCorreo(String correo);

    // ===========================
    // CARGA MASIVA DE USUARIOS
    // ===========================

    byte[] generarPlantillaExcelPorRol(String rol);

    List<String> cargarUsuariosDesdeExcel(String rol, MultipartFile archivo);

    // ================================
    // MÉTODOS EXPORTACIÓN
    // ================================

    // Graficas de usuarios por localidad y rol
    Map<String, Map<String, Long>> obtenerUsuariosPorLocalidadYRol();

    List<Object[]> obtenerUsuariosPorBarrioYLocalidad();

    // Exportar usuarios a Excel y PDF con filtros opcionales
    void exportUsuariosToExcel(String nombre, String correo, String documento, OutputStream os) throws IOException;

    void exportUsuariosToPDF(String nombre, String correo, String documento, OutputStream os)
            throws IOException, DocumentException;

}

package com.EcoSoftware.Scrum6.Security;

import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired

    private TokenJWT tokenJWT;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String method = request.getMethod();

        System.out.println("=== FILTER DEBUG ===");
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);

        // Rutas públicas COMPLETAS
        if (path.equals("/api/auth/login") ||
                (path.equals("/api/personas/registro") && "POST".equalsIgnoreCase(method)) ||
                (path.equals("/api/personas") && "GET".equalsIgnoreCase(method)) ||
                path.equals("/api/personas/test-public") ||
                path.equals("/api/personas/test-registro") || (path.equals("/api/roles")) ||
                (path.equals("/api/roles/crear")) || (path.equals("/api/noticias")) || (path.equals("/api/capacitaciones"))) {
            System.out.println("Ruta pública - permitiendo acceso");
            filterChain.doFilter(request, response);
            return;
        }

        // Para otras rutas, verificar token
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No token found or invalid format - continuing chain");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!tokenJWT.validarToken(token)) {
            System.out.println("Token inválido");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        String correo = tokenJWT.obtenerCorreoDesdeToken(token);
        var usuario = usuarioRepository.findByCorreoAndEstadoTrue(correo);

        // Permitir subir documentos aunque no esté aprobado
        boolean esSubidaDocumento = path.matches("/api/personas/\\d+/documentos");

        if (usuario.get().getEstadoRegistro() != com.EcoSoftware.Scrum6.Enums.EstadoRegistro.APROBADO
                && !esSubidaDocumento) {

            System.out.println("Usuario no aprobado: " + correo);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Tu cuenta aún no ha sido aprobada\"}");
            return;
        }

        // Configurar autenticación
        var rolNombre = usuario.get().getRol().getNombre();
        var authority = new SimpleGrantedAuthority("ROLE_" + rolNombre.toUpperCase());

        User userDetails = new User(
                usuario.get().getCorreo(),
                usuario.get().getContrasena(),
                Collections.singletonList(authority));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("Autenticación exitosa para: " + correo);
        filterChain.doFilter(request, response);
    }
}

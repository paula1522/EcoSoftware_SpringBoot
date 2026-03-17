package com.EcoSoftware.Scrum6.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.EcoSoftware.Scrum6.Security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivar CSRF EXPLÍCITAMENTE
                .csrf(csrf -> csrf.disable())
                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configurar autorizaciones
                .authorizeHttpRequests(auth -> auth
        // ENDPOINTS PÚBLICOS - AUTENTICACIÓN
        .requestMatchers(
                "/api/auth/**",
                "/api/google/**",
                "/api/roles/**",
                "/api/personas/registro",
                "/api/personas",
                "/api/personas/test-public",
                "/api/personas/test-registro",
                "/error")
        .permitAll()

        // POST para documentos
        .requestMatchers(HttpMethod.POST, "/api/personas/*/documentos").permitAll()

        // ENDPOINTS PÚBLICOS - LECTURA
        .requestMatchers(HttpMethod.GET, "/api/capacitaciones/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/noticias/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/puntos/**").permitAll()

        // ENDPOINTS PÚBLICOS - RUTEO Y GEOLOCALIZACIÓN
        .requestMatchers(HttpMethod.GET, "/api/route/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/nearest/**").permitAll()

        // TODO LO DEMÁS REQUIERE TOKEN
        .anyRequest().authenticated()
)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración CORS correcta: no mezclar allowCredentials(true) con allowedOrigins("*")
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origen específico (no wildcard) porque usamos credenciales
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Headers específicos (no wildcard) porque usamos credenciales
        configuration.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "Accept",
                "X-Requested-With",
                "X-CSRF-Token"));

        // Permitir credenciales (JWT en headers)
        configuration.setAllowCredentials(true);

        // Cache de preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

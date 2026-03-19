package com.EcoSoftware.Scrum6.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenJWT {

    // Clave secreta fija (desde application.properties o variable de entorno)
    // Usar clave fija evita que todos los tokens se invaliden al reiniciar la app
    @Value("${jwt.secret:EcoSoftware2026#SecretKeyJWT_MustBe32CharsMin!!}")
    private String jwtSecret;

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Tiempo de expiración ( 24 horas)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Genera un token JWT para el usuario autenticado
     */
    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Obtiene el correo desde el token
     */
    public String obtenerCorreoDesdeToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     *  Valida si el token es correcto y no está vencido
     */
    public boolean validarToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *  Obtiene los claims (datos internos del token)
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.EcoSoftware.Scrum6.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenJWT {

    // Clave secreta para firmar el token
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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
                .signWith(SECRET_KEY)
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
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

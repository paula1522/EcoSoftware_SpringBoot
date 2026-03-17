package com.EcoSoftware.Scrum6.Controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador de excepciones para desarrollo: devuelve mensaje y stacktrace en la respuesta.
 * NO usar en producción.
 */
@ControllerAdvice
public class DevExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAnyException(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();

        // Construir un body sencillo con message y stacktrace
        String body = "{\"error\": \"" + ex.getClass().getName() + ": " + ex.getMessage() + "\",\"stack\": \"" +
                stack.replace("\n", "\\n").replace("\"", "\\\"") + "\"}";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(body);
    }
}

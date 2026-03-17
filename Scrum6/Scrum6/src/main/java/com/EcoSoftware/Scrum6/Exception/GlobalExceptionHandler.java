package com.EcoSoftware.Scrum6.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidacionCapacitacionException.class)
    public ResponseEntity<?> handleValidacionException(ValidacionCapacitacionException ex) {

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("message", ex.getMessage());
        respuesta.put("duplicadas", ex.getDuplicadas());

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("message", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


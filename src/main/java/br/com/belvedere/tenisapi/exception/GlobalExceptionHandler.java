package br.com.belvedere.tenisapi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Verifica se a causa raiz contém o nome da nossa constraint
        if (ex.getMostSpecificCause().getMessage().contains("no_overlapping_bookings")) {
            Map<String, String> body = Map.of("error", "Este horário já está reservado ou em conflito com outra reserva.");
            return new ResponseEntity<>(body, HttpStatus.CONFLICT); // 409 Conflict
        }
        // Para outras violações de integridade, retorna um erro genérico
        Map<String, String> body = Map.of("error", "Erro de integridade dos dados.");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
         Map<String, String> body = Map.of("error", ex.getMessage());
         return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST); // 400 Bad Request
    }
}
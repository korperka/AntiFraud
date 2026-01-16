package net.korperka.antifraud.config;

import jakarta.servlet.http.HttpServletRequest;
import net.korperka.antifraud.dto.response.APIErrorResponse;
import net.korperka.antifraud.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIErrorResponse> handleBadRequest(HttpMessageNotReadableException ex, HttpServletRequest request) {

        APIErrorResponse response = APIErrorResponse.builder()
                .code("BAD_REQUEST")
                .message("Невалидный JSON")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .details(Map.of("hint", "Проверьте запятые/кавычки и типы данных"))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<APIErrorResponse.ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> APIErrorResponse.ValidationError.builder()
                        .field(error.getField())
                        .issue(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        APIErrorResponse response = APIErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .message("Некоторые поля не прошли валидацию")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .fieldErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<APIErrorResponse> handleUserExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        Map<String, Object> details = Map.of(
                "field", "email",
                "value", ex.getEmail()
        );

        APIErrorResponse response = APIErrorResponse.builder()
                .code("EMAIL_ALREADY_EXISTS")
                .message(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
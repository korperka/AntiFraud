package net.korperka.antifraud.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import net.korperka.antifraud.dto.response.APIErrorResponse;
import net.korperka.antifraud.exception.InvalidCredentialsException;
import net.korperka.antifraud.exception.UserAlreadyExistsException;
import net.korperka.antifraud.exception.UserDeactivatedException;
import net.korperka.antifraud.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {

        APIErrorResponse response = APIErrorResponse.builder()
                .code("FORBIDDEN")
                .message("Недостаточно прав (нужна другая роль)")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleAccessDenied(UserNotFoundException ex, HttpServletRequest request) {

        APIErrorResponse response = APIErrorResponse.builder()
                .code("NOT_FOUND")
                .message("Пользователь не найден")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<APIErrorResponse> handleUserInactive(UserDeactivatedException ex, HttpServletRequest request) {
        APIErrorResponse response = APIErrorResponse.builder()
                .code("USER_INACTIVE")
                .message("Пользователь деактивирован")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.LOCKED).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<APIErrorResponse> handleUnauthorized(InvalidCredentialsException ex, HttpServletRequest request) {
        APIErrorResponse response = APIErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Токен отсутствует или невалиден")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<APIErrorResponse> handleMethodValidation(HandlerMethodValidationException ex, HttpServletRequest request) {
        List<APIErrorResponse.ValidationError> errors = ex.getAllValidationResults().stream()
                .map(result -> APIErrorResponse.ValidationError.builder()
                        .field(result.getMethodParameter().getParameterName())
                        .issue(result.getResolvableErrors().get(0).getDefaultMessage())
                        .rejectedValue(result.getArgument())
                        .build())
                .toList();

        APIErrorResponse response = APIErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .message("Ошибка валидации параметров")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .fieldErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIErrorResponse> handleParamsError(ConstraintViolationException ex, HttpServletRequest request) {
        List<APIErrorResponse.ValidationError> errors = ex.getConstraintViolations().stream()
                .map(violation -> APIErrorResponse.ValidationError.builder()
                        .field(violation.getPropertyPath().toString())
                        .issue(violation.getMessage())
                        .rejectedValue(violation.getInvalidValue())
                        .build())
                .toList();

        APIErrorResponse response = APIErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .message("Ошибка валидации параметров")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .fieldErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
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
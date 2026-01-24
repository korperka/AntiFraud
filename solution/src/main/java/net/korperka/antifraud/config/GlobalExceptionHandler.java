package net.korperka.antifraud.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import net.korperka.antifraud.dto.response.ApiErrorResponse;
import net.korperka.antifraud.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("FORBIDDEN")
                .message("Forbidden")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(NotFoundException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("NOT_FOUND")
                .message("Ресурс не найден")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .details(Map.of("userId", ex.getUserId()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<ApiErrorResponse> handleUserInactive(UserDeactivatedException ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("USER_INACTIVE")
                .message("Пользователь деактивирован")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.LOCKED).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(InvalidCredentialsException ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Токен отсутствует или невалиден")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
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
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(HandlerMethodValidationException ex, HttpServletRequest request) {
        List<ApiErrorResponse.ValidationError> errors = ex.getAllValidationResults().stream()
                .map(result -> ApiErrorResponse.ValidationError.builder()
                        .field(result.getMethodParameter().getParameterName())
                        .issue(result.getResolvableErrors().get(0).getDefaultMessage())
                        .rejectedValue(result.getArgument())
                        .build())
                .toList();

        ApiErrorResponse response = ApiErrorResponse.builder()
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
    public ResponseEntity<ApiErrorResponse> handleParamsError(ConstraintViolationException ex, HttpServletRequest request) {
        List<ApiErrorResponse.ValidationError> errors = ex.getConstraintViolations().stream()
                .map(violation -> ApiErrorResponse.ValidationError.builder()
                        .field(violation.getPropertyPath().toString())
                        .issue(violation.getMessage())
                        .rejectedValue(violation.getInvalidValue())
                        .build())
                .toList();

        ApiErrorResponse response = ApiErrorResponse.builder()
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
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiErrorResponse.ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ApiErrorResponse.ValidationError.builder()
                        .field(error.getField())
                        .issue(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .message("Некоторые поля не прошли валидацию")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .fieldErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(DateFormatException.class)
    public ResponseEntity<ApiErrorResponse> handleStatusException(DateFormatException ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .message("Некоторые поля не прошли валидацию")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRuleExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        Map<String, Object> details = Map.of(
                "field", "email",
                "value", ex.getEmail()
        );

        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("EMAIL_ALREADY_EXISTS")
                .message(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(FraudRuleAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRuleExists(FraudRuleAlreadyExistsException ex, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .code("RULE_NAME_ALREADY_EXISTS")
                .message(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
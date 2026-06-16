package net.korperka.antifraud.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.response.ApiErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Токен отсутствует или невалиден")
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
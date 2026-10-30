package com.devkit.hsd.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String responseBody = """
                {
                  "status": 401,
                  "error": "Unauthorized",
                  "message": "Kimlik doğrulama başarısız.",
                  "path": "%s",
                  "error_code": "AUTHENTICATION_FAILED"
                }
                """.formatted(request.getRequestURI());

            response.getWriter().write(responseBody);
    }
}

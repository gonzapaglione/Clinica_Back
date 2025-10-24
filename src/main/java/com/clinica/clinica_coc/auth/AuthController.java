package com.clinica.clinica_coc.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.clinica_coc.DTO.OdontologoRequest;
import com.clinica.clinica_coc.DTO.RegisterRecepcionistaRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            logger.debug("Login attempt for email={}", request.getEmail());
            AuthResponse resp = authService.login(request);
            return ResponseEntity.ok(resp);
        } catch (AuthenticationException ae) {
            logger.info("Authentication failed for email={}", request.getEmail());
            return ResponseEntity.status(401).body(Map.of("message", "Credenciales inválidas"));
        }

    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));

    }

    // Endpoints under /auth/admin/** should be accessible only by Admin (configured
    // in SecurityConfig)
    @PostMapping("/admin/register/odontologo")
    public ResponseEntity<AuthResponse> registerOdontologo(
            @RequestBody OdontologoRequest request) {
        return ResponseEntity.ok(authService.registerOdontologo(request));
    }

    @PostMapping("/admin/register/recepcionista")
    public ResponseEntity<AuthResponse> registerRecepcionista(@RequestBody RegisterRecepcionistaRequest request) {
        return ResponseEntity.ok(authService.registerRecepcionista(request));
    }

}

package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.LoginRequest;
import com.pragma.powerup.application.dto.response.AuthenticatedUserResponse;
import com.pragma.powerup.application.dto.response.TokenResponse;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.spi.IAuthContextPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final IAuthHandler authHandler;
    private final IAuthContextPort authContextPort;

    @Operation(summary = "Inicio de sesión y obtención de token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse token = authHandler.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> getAuthenticatedUser() {
        String userId = authContextPort.getAuthenticatedUserId();
        String role = authContextPort.getAuthenticatedUserRole();

        AuthenticatedUserResponse response = new AuthenticatedUserResponse(userId, role);

        return ResponseEntity.ok(response);
    }
}

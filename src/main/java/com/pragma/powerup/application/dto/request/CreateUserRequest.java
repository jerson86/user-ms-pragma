package com.pragma.powerup.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    @Pattern(regexp = "\\d+", message = "El documento debe ser numérico")
    private String documento;

    @NotBlank
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = "El telefono debe tener máximo 13 dígitos y puede iniciar con +")
    private String telefono;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    @Size(min = 6, message = "La clave debe tener mínimo 6 caracteres")
    private String clave;

    private String role;
}

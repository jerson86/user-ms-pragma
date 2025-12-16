package com.pragma.powerup.domain.model;

import com.pragma.powerup.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
    private Role role;
    Long restaurantId;
}

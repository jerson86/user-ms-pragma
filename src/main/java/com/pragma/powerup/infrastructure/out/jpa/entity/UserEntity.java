package com.pragma.powerup.infrastructure.out.jpa.entity;

import com.pragma.powerup.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String documento;

    private String telefono;

    private LocalDate fechaNacimiento;

    @Column(unique = true, nullable = false)
    private String correo;

    private String clave;

    @Enumerated(EnumType.STRING)
    private Role role;
}

package com.pragma.powerup.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BusinessMessage {
    USER_EMAIL_ALREADY_EXISTS("El email ya se encuentra registrado"),
    USER_DOCUMENT_ALREADY_EXISTS("El documento de identidad ya se encuentra registrado"),
    USER_IS_NOT_ADULT("El usuario debe ser mayor de edad");

    private final String message;
}

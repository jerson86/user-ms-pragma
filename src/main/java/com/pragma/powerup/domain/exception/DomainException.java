package com.pragma.powerup.domain.exception;

import com.pragma.powerup.domain.enums.BusinessMessage;
import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final BusinessMessage businessMessage;

    public DomainException(BusinessMessage businessMessage) {
        super(businessMessage.getMessage());
        this.businessMessage = businessMessage;
    }
}

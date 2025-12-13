package com.pragma.powerup.domain.spi;

public interface IAuthContextPort {
    String getAuthenticatedUserRole();
    String getAuthenticatedUserId();
}

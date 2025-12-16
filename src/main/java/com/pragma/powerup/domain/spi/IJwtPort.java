package com.pragma.powerup.domain.spi;

public interface IJwtPort {
    String createToken(String subject, String role, Long idRestaurant);
    String getUserIdFromToken(String token);
    String getRoleFromToken(String token);
}

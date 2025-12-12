package com.pragma.powerup.domain.spi;

public interface IPasswordEncoderPort {
    boolean matches(String password, String passwordBd);
    String encode(String rawPassword);
}

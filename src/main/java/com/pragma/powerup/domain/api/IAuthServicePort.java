package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.TokenModel;

public interface IAuthServicePort {
    TokenModel login(String email, String password);
}

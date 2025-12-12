package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.LoginRequest;
import com.pragma.powerup.application.dto.response.TokenResponse;

public interface IAuthHandler {
    TokenResponse login(LoginRequest loginRequest);
}

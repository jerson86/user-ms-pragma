package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.LoginRequest;
import com.pragma.powerup.application.dto.response.TokenResponse;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.application.mapper.IAuthMapper;
import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.model.TokenModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {

    private final IAuthServicePort authServicePort; // El Use Case
    private final IAuthMapper authMapper;

    @Override
    public TokenResponse login(LoginRequest loginRequest) {

        TokenModel tokenModel = authServicePort.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        return authMapper.toResponse(tokenModel);
    }
}
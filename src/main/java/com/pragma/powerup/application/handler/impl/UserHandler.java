package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.CreateUserRequest;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandler implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final IUserRequestMapper objectRequestMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(CreateUserRequest createUserRequest) {
        UserModel userModel = objectRequestMapper.toObject(createUserRequest);
        userModel.setClave(passwordEncoder.encode(createUserRequest.getClave()));
        userServicePort.saveUser(userModel);
    }

    @Override
    public UserModel getUserId(Long id) {
        return userServicePort.getUserId(id);
    }
}
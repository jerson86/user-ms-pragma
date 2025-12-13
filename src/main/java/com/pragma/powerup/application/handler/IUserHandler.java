package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CreateUserRequest;
import com.pragma.powerup.domain.model.UserModel;

public interface IUserHandler {
    void saveUser(CreateUserRequest createUserRequest);
    UserModel getUserId(Long id);
}
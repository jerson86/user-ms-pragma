package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CreateOwnerRequest;
import com.pragma.powerup.domain.model.UserModel;

public interface IUserHandler {
    void saveUser(CreateOwnerRequest createOwnerRequest);
    UserModel getUserId(Long id);
}
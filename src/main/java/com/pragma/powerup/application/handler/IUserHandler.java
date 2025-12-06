package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.CreateOwnerRequest;

public interface IUserHandler {
    void saveUser(CreateOwnerRequest createOwnerRequest);
}
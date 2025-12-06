package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserPersistencePort {
    UserModel saveUser(UserModel userModel);
    UserModel findByCorreo(String correo);
    boolean existsByDocumento(String documento);
}
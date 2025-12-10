package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

import java.time.LocalDate;

import static com.pragma.powerup.domain.util.CommonConstant.AGE_MIN_ADULT;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;

    public UserUseCase(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public void saveUser(UserModel userModel) {
        if (userPersistencePort.findByCorreo(userModel.getCorreo()) != null) {
            throw new DomainException(BusinessMessage.USER_EMAIL_ALREADY_EXISTS);
        }

        if (userModel.getFechaNacimiento().isAfter(LocalDate.now().minusYears(AGE_MIN_ADULT))) {
            throw new DomainException(BusinessMessage.USER_IS_NOT_ADULT);
        }

        if (userPersistencePort.existsByDocumento(userModel.getDocumento())) {
            throw new DomainException(BusinessMessage.USER_DOCUMENT_ALREADY_EXISTS);
        }

        userPersistencePort.saveUser(userModel);
    }

    @Override
    public UserModel getUserId(Long id) {
        return userPersistencePort.findById(id);
    }
}
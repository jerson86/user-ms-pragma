package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.enums.Role;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IAuthContextPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

import java.time.LocalDate;

import static com.pragma.powerup.domain.util.CommonConstant.AGE_MIN_ADULT;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IAuthContextPort authContextPort;

    public UserUseCase(IUserPersistencePort userPersistencePort, IAuthContextPort authContextPort) {
        this.userPersistencePort = userPersistencePort;
        this.authContextPort = authContextPort;
    }

    @Override
    public void saveUser(UserModel userModel) {

        String callingRole = authContextPort.getAuthenticatedUserRole();

        if (!Role.ADMIN.name().equals(callingRole) && !Role.OWNER.name().equals(callingRole) && !Role.CLIENT.name().equals(callingRole)) {
            throw new DomainException(BusinessMessage.AUTH_INVALID_CREDENTIALS);
        }

        if (!Role.ADMIN.name().equals(callingRole)) {
            userModel.setRole(Role.OWNER);
        }

        if (!Role.OWNER.name().equals(callingRole)) {
            userModel.setRole(Role.EMPLOYEE);
        }

        if (!Role.CLIENT.name().equals(callingRole)) {
            userModel.setRole(Role.CLIENT);
        }

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
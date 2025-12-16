package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.TokenModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IJwtPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IJwtPort jwtPort;
    private final IPasswordEncoderPort IPasswordEncoderPort;

    @Override
    public TokenModel login(String email, String password) {

        UserModel user = userPersistencePort.findByCorreo(email);

        if (user == null) {
            throw new DomainException(BusinessMessage.AUTH_INVALID_CREDENTIALS);
        }

        if (!IPasswordEncoderPort.matches(password, user.getClave())) {
            throw new DomainException(BusinessMessage.AUTH_INVALID_CREDENTIALS);
        }

        String token = jwtPort.createToken(
                user.getId().toString(),
                user.getRole().name(),
                user.getRestaurantId()
        );

        return new TokenModel(token);
    }
}
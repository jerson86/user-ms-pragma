package com.pragma.powerup.domain;

import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.enums.Role;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.TokenModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IJwtPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.usecase.AuthUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;
    @Mock
    private IJwtPort jwtPort;
    @Mock
    private IPasswordEncoderPort IPasswordEncoderPort;

    @InjectMocks
    private AuthUseCase authUseCase;

    private final String EMAIL = "test@pragma.com";
    private final String RAW_PASSWORD = "Password123";
    private final String ENCODED_PASSWORD = "$2a$10$encodedHash";

    private UserModel mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserModel(
                1L, "Test", "User", "123456789", "3122222222", null, EMAIL, ENCODED_PASSWORD, Role.ADMIN
        );
    }

    @Test
    void login_SuccessfulScenario_ReturnsTokenModel() {
        // ARRANGE
        when(userPersistencePort.findByCorreo(EMAIL)).thenReturn(mockUser);
        when(IPasswordEncoderPort.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        String GENERATED_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        when(jwtPort.createToken(anyString(), anyString())).thenReturn(GENERATED_JWT);

        // ACT
        TokenModel result = authUseCase.login(EMAIL, RAW_PASSWORD);

        // ASSERT
        assertNotNull(result);
        assertEquals(GENERATED_JWT, result.getJwt());

        verify(IPasswordEncoderPort, times(1)).matches(RAW_PASSWORD, ENCODED_PASSWORD);

        verify(jwtPort, times(1)).createToken(
                mockUser.getId().toString(),
                mockUser.getRole().name()
        );
    }

    @Test
    void login_ThrowsException_WhenUserNotFound() {
        // ARRANGE
        when(userPersistencePort.findByCorreo(EMAIL)).thenReturn(null);

        // ACT & ASSERT
        DomainException ex = assertThrows(DomainException.class, () ->
                authUseCase.login(EMAIL, RAW_PASSWORD)
        );
        assertEquals(BusinessMessage.AUTH_INVALID_CREDENTIALS.getMessage(), ex.getMessage());

        verify(IPasswordEncoderPort, never()).matches(anyString(), anyString());
        verify(jwtPort, never()).createToken(anyString(), anyString());
    }

    @Test
    void login_ThrowsException_WhenPasswordIsIncorrect() {
        // ARRANGE
        when(userPersistencePort.findByCorreo(EMAIL)).thenReturn(mockUser);
        when(IPasswordEncoderPort.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        // ACT & ASSERT
        DomainException ex = assertThrows(DomainException.class, () ->
                authUseCase.login(EMAIL, RAW_PASSWORD)
        );
        assertEquals(BusinessMessage.AUTH_INVALID_CREDENTIALS.getMessage(), ex.getMessage());

        verify(jwtPort, never()).createToken(anyString(), anyString());
    }
}
package com.pragma.powerup.application;

import com.pragma.powerup.application.dto.request.LoginRequest;
import com.pragma.powerup.application.dto.response.TokenResponse;
import com.pragma.powerup.application.handler.impl.AuthHandler;
import com.pragma.powerup.application.mapper.IAuthMapper;
import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.TokenModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private IAuthServicePort authServicePort;

    @Mock
    private IAuthMapper authMapper;

    @InjectMocks
    private AuthHandler authHandler;

    private LoginRequest mockRequest;
    private TokenModel mockTokenModel;
    private TokenResponse mockTokenResponse;

    private final String EMAIL = "test@pragma.com";
    private final String RAW_PASSWORD = "Password123";
    private final String GENERATED_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    @BeforeEach
    void setUp() {
        mockRequest = new LoginRequest();
        mockRequest.setEmail(EMAIL);
        mockRequest.setPassword(RAW_PASSWORD);

        mockTokenModel = new TokenModel(GENERATED_JWT);
        mockTokenResponse = new TokenResponse(GENERATED_JWT, "Bearer");
    }

    @Test
    void login_SuccessfulScenario_CallsUseCaseAndMapper() {
        // ARRANGE
        when(authServicePort.login(EMAIL, RAW_PASSWORD)).thenReturn(mockTokenModel);

        when(authMapper.toResponse(mockTokenModel)).thenReturn(mockTokenResponse);

        // ACT
        TokenResponse result = authHandler.login(mockRequest);

        // ASSERT
        assertNotNull(result);
        assertEquals(GENERATED_JWT, result.getJwt());

        verify(authServicePort, times(1)).login(EMAIL, RAW_PASSWORD);
        verify(authMapper, times(1)).toResponse(mockTokenModel);
    }

    @Test
    void login_ThrowsException_WhenCredentialsAreInvalid() {
        // ARRANGE
        doThrow(new DomainException(BusinessMessage.AUTH_INVALID_CREDENTIALS))
                .when(authServicePort).login(EMAIL, RAW_PASSWORD);

        // ACT & ASSERT
        DomainException ex = assertThrows(DomainException.class, () ->
                authHandler.login(mockRequest)
        );
        assertEquals(BusinessMessage.AUTH_INVALID_CREDENTIALS.getMessage(), ex.getMessage());

        verify(authMapper, never()).toResponse(any(TokenModel.class));
    }
}
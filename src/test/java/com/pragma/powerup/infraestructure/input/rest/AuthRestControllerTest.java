package com.pragma.powerup.infraestructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.LoginRequest;
import com.pragma.powerup.application.dto.response.TokenResponse;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.infrastructure.configuration.BeanConfiguration;
import com.pragma.powerup.infrastructure.input.rest.AuthRestController;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import com.pragma.powerup.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        BeanConfiguration.class,
        JwtAuthenticationFilter.class
})
class AuthRestControllerTest {

    private final String BASE_URL = "/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAuthHandler authHandler;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private IUserEntityMapper userEntityMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private LoginRequest validRequest;
    private TokenResponse mockResponse;
    private LoginRequest invalidEmailRequest;

    private final String VALID_EMAIL = "test@pragma.com";
    private final String VALID_PASSWORD = "Password123";
    private final String GENERATED_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.ROLE";

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest();
        validRequest.setEmail(VALID_EMAIL);
        validRequest.setPassword(VALID_PASSWORD);

        mockResponse = new TokenResponse(GENERATED_JWT, "Bearer");

        invalidEmailRequest = new LoginRequest();
        invalidEmailRequest.setEmail("notanemail");
        invalidEmailRequest.setPassword(VALID_PASSWORD);
    }

    @Test
    void login_ValidCredentials_ReturnsOkStatusAndToken() throws Exception {
        // ARRANGE
        when(authHandler.login(any(LoginRequest.class))).thenReturn(mockResponse);

        // ACT & ASSERT
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .with(SecurityMockMvcRequestPostProcessors.anonymous())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(GENERATED_JWT))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authHandler, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorizedStatus() throws Exception {
        // ARRANGE
        doThrow(new DomainException(BusinessMessage.AUTH_INVALID_CREDENTIALS))
                .when(authHandler).login(any(LoginRequest.class));

        // ACT & ASSERT
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .with(SecurityMockMvcRequestPostProcessors.anonymous())
                )
                .andExpect(status().isUnauthorized());

        verify(authHandler, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidEmailFormat_ReturnsBadRequestStatus() throws Exception {

        // ACT & ASSERT
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest))
                        .with(SecurityMockMvcRequestPostProcessors.anonymous())
                )
                .andExpect(status().isBadRequest());

        verify(authHandler, never()).login(any(LoginRequest.class));
    }
}

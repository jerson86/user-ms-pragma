package com.pragma.powerup.infraestructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.CreateUserRequest;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.spi.IAuthContextPort;
import com.pragma.powerup.infraestructure.configuration.TestSecurityConfig;
import com.pragma.powerup.infrastructure.configuration.BeanConfiguration;
import com.pragma.powerup.infrastructure.input.rest.UserRestController;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import com.pragma.powerup.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@Import({
        TestSecurityConfig.class,
        BeanConfiguration.class,
        JwtAuthenticationFilter.class
})
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserHandler userHandler;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private IUserEntityMapper userEntityMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private IAuthContextPort authContextPort;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateUserRequest buildValidRequest() {
        CreateUserRequest req = new CreateUserRequest();
        req.setNombre("Juan");
        req.setApellido("Perez");
        req.setDocumento("123456789");
        req.setTelefono("+573001112233");
        req.setFechaNacimiento(LocalDate.parse("1990-01-01"));
        req.setCorreo("test@mail.com");
        req.setClave("password123");
        req.setRole("ADMIN");
        return req;
    }

    @Test
    void saveOwner_WhenAdmin_ShouldReturn201() throws Exception {
        // ARRANGE
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ADMIN");
        CreateUserRequest req = buildValidRequest();

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("testUser").roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(userHandler, times(1)).saveUser(any(CreateUserRequest.class));
    }

    @Test
    void saveOwner_WhenNotAdmin_ShouldReturn403() throws Exception {
        // ARRANGE
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("OTHER");
        CreateUserRequest req = buildValidRequest();
        req.setRole("OTHER");
        doThrow(new DomainException(BusinessMessage.AUTH_INVALID_CREDENTIALS))
                .when(userHandler).saveUser(any(CreateUserRequest.class));

        when(authContextPort.getAuthenticatedUserRole()).thenReturn("OTHER");

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("testUser").roles("OTHER"))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        verify(userHandler, times(1)).saveUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveOwner_WhenInvalidRequest_ShouldReturn400() throws Exception {

        CreateUserRequest invalidReq = new CreateUserRequest();
        invalidReq.setNombre("");

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test")
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());

        verify(userHandler, times(0)).saveUser(any());
    }
}
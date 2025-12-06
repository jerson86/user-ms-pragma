package com.pragma.powerup.infraestructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.CreateOwnerRequest;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.infraestructure.configuration.TestSecurityConfig;
import com.pragma.powerup.infrastructure.input.rest.UserRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@Import(TestSecurityConfig.class)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserHandler userHandler;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOwnerRequest buildValidRequest() {
        CreateOwnerRequest req = new CreateOwnerRequest();
        req.setNombre("Juan");
        req.setApellido("Perez");
        req.setDocumento("123456789");
        req.setTelefono("+573001112233");
        req.setFechaNacimiento(LocalDate.parse("1990-01-01"));
        req.setCorreo("test@mail.com");
        req.setClave("password123");
        return req;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveOwner_WhenAdmin_ShouldReturn201() throws Exception {

        CreateOwnerRequest req = buildValidRequest();

        mockMvc.perform(post("/api/v1/admin/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(userHandler, times(1)).saveUser(any(CreateOwnerRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void saveOwner_WhenNotAdmin_ShouldReturn403() throws Exception {

        mockMvc.perform(post("/api/v1/admin/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isForbidden());

        verify(userHandler, times(0)).saveUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveOwner_WhenInvalidRequest_ShouldReturn400() throws Exception {

        CreateOwnerRequest invalidReq = new CreateOwnerRequest();
        invalidReq.setNombre("");

        mockMvc.perform(post("/api/v1/admin/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());

        verify(userHandler, times(0)).saveUser(any());
    }
}
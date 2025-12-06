package com.pragma.powerup.application;

import com.pragma.powerup.application.dto.request.CreateOwnerRequest;
import com.pragma.powerup.application.handler.impl.UserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IUserRequestMapper objectRequestMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserHandler userHandler;

    private CreateOwnerRequest request;
    private UserModel userModel;

    @BeforeEach
    void setup() {
        request = new CreateOwnerRequest();
        request.setNombre("Carlos");
        request.setApellido("Ramirez");
        request.setCorreo("owner@test.com");
        request.setClave("1234");

        userModel = new UserModel();
        userModel.setCorreo("owner@test.com");
    }

    @Test
    void saveUser_ShouldMapEncodeAndCallService() {

        // Arrange
        when(objectRequestMapper.toObject(request)).thenReturn(userModel);
        when(passwordEncoder.encode("1234")).thenReturn("ENCODED_PASSWORD");

        // Act
        userHandler.saveUser(request);

        // Assert
        assertEquals("ENCODED_PASSWORD", userModel.getClave());
        verify(objectRequestMapper, times(1)).toObject(request);
        verify(passwordEncoder, times(1)).encode("1234");

        // Capture argument to ensure correct object passed
        ArgumentCaptor<UserModel> captor = ArgumentCaptor.forClass(UserModel.class);
        verify(userServicePort, times(1)).saveUser(captor.capture());

        assertEquals("ENCODED_PASSWORD", captor.getValue().getClave());
    }

    @Test
    void saveUser_WhenServiceThrowsException_ShouldPropagate() {
        // Arrange
        when(objectRequestMapper.toObject(request)).thenReturn(userModel);
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        doThrow(new RuntimeException("DB error")).when(userServicePort).saveUser(any());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userHandler.saveUser(request)
        );

        assertEquals("DB error", ex.getMessage());
        verify(userServicePort).saveUser(any());
    }

    @Test
    void saveUser_ShouldFailIfMapperReturnsNull() {
        // Arrange
        when(objectRequestMapper.toObject(request)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userHandler.saveUser(request));
    }
}

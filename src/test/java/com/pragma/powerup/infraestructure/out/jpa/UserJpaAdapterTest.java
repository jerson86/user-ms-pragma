package com.pragma.powerup.infraestructure.out.jpa;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserJpaAdapterTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IUserEntityMapper mapper;

    @InjectMocks
    private UserJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_ShouldReturnMappedModel() {
        // Arrange
        UserModel inputModel = new UserModel();
        UserEntity mappedEntity = new UserEntity();
        UserEntity savedEntity = new UserEntity();
        UserModel mappedOutputModel = new UserModel();

        when(mapper.toEntity(inputModel)).thenReturn(mappedEntity);
        when(userRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toObjectModel(savedEntity)).thenReturn(mappedOutputModel);

        // Act
        UserModel result = adapter.saveUser(inputModel);

        // Assert
        assertNotNull(result);
        assertEquals(mappedOutputModel, result);

        verify(mapper).toEntity(inputModel);
        verify(userRepository).save(mappedEntity);
        verify(mapper).toObjectModel(savedEntity);
    }

    @Test
    void findByCorreo_WhenUserExists_ShouldReturnUserModel() {
        // Arrange
        String correo = "test@mail.com";
        UserEntity entity = new UserEntity();
        UserModel expectedModel = new UserModel();

        when(userRepository.findByCorreo(correo)).thenReturn(Optional.of(entity));
        when(mapper.toObjectModel(entity)).thenReturn(expectedModel);

        // Act
        UserModel result = adapter.findByCorreo(correo);

        // Assert
        assertNotNull(result);
        assertEquals(expectedModel, result);

        verify(userRepository).findByCorreo(correo);
        verify(mapper).toObjectModel(entity);
    }

    @Test
    void findByCorreo_WhenNotFound_ShouldReturnNull() {
        // Arrange
        String correo = "no@exist.com";

        when(userRepository.findByCorreo(correo)).thenReturn(Optional.empty());

        // Act
        UserModel result = adapter.findByCorreo(correo);

        // Assert
        assertNull(result);
        verify(userRepository).findByCorreo(correo);
        verify(mapper, never()).toObjectModel(any());
    }
    
    @Test
    void existsByDocumento_ShouldReturnBoolean() {
        // Arrange
        String documento = "123456";
        when(userRepository.existsByDocumento(documento)).thenReturn(true);

        // Act
        boolean exists = adapter.existsByDocumento(documento);

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByDocumento(documento);
    }
}

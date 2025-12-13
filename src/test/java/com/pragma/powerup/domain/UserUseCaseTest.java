package com.pragma.powerup.domain;

import com.pragma.powerup.domain.enums.BusinessMessage;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IAuthContextPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.pragma.powerup.domain.util.CommonConstant.AGE_MIN_ADULT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort persistencePort;

    @Mock
    private IAuthContextPort authContextPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserModel user;

    @BeforeEach
    void setup() {
        user = new UserModel();
        user.setCorreo("test@mail.com");
        user.setDocumento("123456789");
        user.setFechaNacimiento(LocalDate.now().minusYears(AGE_MIN_ADULT + 1));
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ADMIN");
    }

    @Test
    void saveUser_WhenEmailExists_ShouldThrowException() {

        // Arrange
        when(persistencePort.findByCorreo("test@mail.com")).thenReturn(new UserModel());

        // Act + Assert
        DomainException ex = assertThrows(
                DomainException.class,
                () -> userUseCase.saveUser(user)
        );

        assertEquals(BusinessMessage.USER_EMAIL_ALREADY_EXISTS.getMessage(), ex.getMessage());

        verify(persistencePort, never()).saveUser(any());
    }

    @Test
    void saveUser_WhenUserIsNotAdult_ShouldThrowException() {

        // Arrange
        user.setFechaNacimiento(LocalDate.now().minusYears(AGE_MIN_ADULT - 1)); // menor de edad
        when(persistencePort.findByCorreo(any())).thenReturn(null);

        // Act + Assert
        DomainException ex = assertThrows(
                DomainException.class,
                () -> userUseCase.saveUser(user)
        );

        assertEquals(BusinessMessage.USER_IS_NOT_ADULT.getMessage(), ex.getMessage());
        verify(persistencePort, never()).saveUser(any());
    }

    @Test
    void saveUser_WhenDocumentExists_ShouldThrowException() {

        // Arrange
        when(persistencePort.findByCorreo(any())).thenReturn(null);
        when(persistencePort.existsByDocumento("123456789")).thenReturn(true);

        // Act + Assert
        DomainException ex = assertThrows(
                DomainException.class,
                () -> userUseCase.saveUser(user)
        );

        assertEquals(BusinessMessage.USER_DOCUMENT_ALREADY_EXISTS.getMessage(), ex.getMessage());

        verify(persistencePort, never()).saveUser(any());
    }

    @Test
    void saveUser_WhenValid_ShouldSaveSuccessfully() {

        // Arrange
        when(persistencePort.findByCorreo(any())).thenReturn(null);
        when(persistencePort.existsByDocumento(any())).thenReturn(false);

        // Act
        userUseCase.saveUser(user);

        // Assert
        verify(persistencePort, times(1)).saveUser(user);
    }

    @Test
    void saveUser_WhenExactly18YearsOld_ShouldBeAdultAndSave() {

        // Arrange
        user.setFechaNacimiento(LocalDate.now().minusYears(AGE_MIN_ADULT));
        when(persistencePort.findByCorreo(any())).thenReturn(null);
        when(persistencePort.existsByDocumento(any())).thenReturn(false);

        // Act
        assertDoesNotThrow(() -> userUseCase.saveUser(user));

        verify(persistencePort).saveUser(user);
    }
}

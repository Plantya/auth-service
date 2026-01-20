// File: AuthServiceTest.java

package fun.plantya.auth.service;

import fun.plantya.auth.common.exception.ConflictException;
import fun.plantya.auth.common.exception.message.ErrorMessage;
import fun.plantya.auth.dto.request.RegisterRequest;
import fun.plantya.auth.dto.response.BaseResponse;
import fun.plantya.auth.dto.response.RegisterResponse;
import fun.plantya.auth.model.User;
import fun.plantya.auth.model.UserRole;
import fun.plantya.auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private AuthRepository authRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        authRepository = mock(AuthRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authService = new AuthService(authRepository, passwordEncoder);
    }

    @Test
    void register_successfulRegistration() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "johndoe@example.com",
                "password123",
                "password123"
        );

        when(authRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(authRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId("12345");
            return user;
        });

        // When
        BaseResponse<RegisterResponse> response = authService.register(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals("12345", response.data().userId());
        assertEquals(request.name(), response.data().name());
        assertEquals(request.email(), response.data().email());
        assertEquals(UserRole.USER, response.data().role());
        verify(authRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_emailAlreadyExists_throwsConflictException() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "Jane Doe",
                "janedoe@example.com",
                "password123",
                "password123"
        );

        when(authRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        // When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> authService.register(request)
        );

        assertEquals(ErrorMessage.EMAIL_ALREADY_EXISTS.getDefaultDetail(), exception.getMessage());
        verify(authRepository, never()).save(any(User.class));
    }
}
package com.kitsune.BanckLoad.application.service;

import com.kitsune.BanckLoad.application.dto.AuthResponseDTO;
import com.kitsune.BanckLoad.application.dto.LoginRequestDTO;
import com.kitsune.BanckLoad.application.dto.RegisterRequestDTO;
import com.kitsune.BanckLoad.domain.model.User;
import com.kitsune.BanckLoad.infrastructure.repository.UserRepository;
import com.kitsune.BanckLoad.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .password("encodedPassword")
                .fullName("Test User")
                .roles(Set.of("USER"))
                .enabled(true)
                .build();
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("newuser@test.com")
                .password("password123")
                .fullName("New User")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken("newuser")).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(anyString());
    }

    @Test
    void testRegister_UsernameExists() {
        // Arrange
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("existinguser")
                .email("newuser@test.com")
                .password("password123")
                .fullName("New User")
                .build();

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));
        assertTrue(exception.getMessage().contains("nombre de usuario ya existe"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        // Arrange
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("existing@test.com")
                .password("password123")
                .fullName("New User")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));
        assertTrue(exception.getMessage().contains("email ya está registrado"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("testuser")
                .password("password123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken("testuser")).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@test.com", response.getEmail());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken("testuser");
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("nonexistent")
                .password("password123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(request));
        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(jwtService, never()).generateToken(anyString());
    }
}


package com.aatzhmair.userservice.service;

import com.aatzhmair.userservice.dto.RegisterRequest;
import com.aatzhmair.userservice.exception.DuplicateUserException;
import com.aatzhmair.userservice.repository.UserRepository;
import com.aatzhmair.userservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @Test
    void register_throwsWhenUsernameOrEmailTaken() {
        var authService = new AuthService(userRepository, passwordEncoder, jwtService);
        var request = new RegisterRequest("jdoe", "jdoe@example.com", "SuperSecret1");
        when(userRepository.existsByUsernameOrEmail("jdoe", "jdoe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateUserException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_hashesPasswordBeforeSaving() {
        var authService = new AuthService(userRepository, passwordEncoder, jwtService);
        var request = new RegisterRequest("jdoe", "jdoe@example.com", "SuperSecret1");

        when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(false);
        when(passwordEncoder.encode("SuperSecret1")).thenReturn("hashed-value");
        when(jwtService.generateToken(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "jdoe", "CUSTOMER")).thenReturn("fake-jwt");
        when(jwtService.expirationMs()).thenReturn(3_600_000L);

        var response = authService.register(request);

        assertThat(response.token()).isEqualTo("fake-jwt");
        verify(userRepository).save(argThat(u -> u.getPasswordHash().equals("hashed-value")));
    }
}
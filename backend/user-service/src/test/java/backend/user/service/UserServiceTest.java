package backend.user.service;

import backend.shared.dto.UserDto;
import backend.shared.util.JwtUtil;
import backend.user.exception.UserNotFoundException;
import backend.user.mapper.UserMapper;
import backend.user.model.User;
import backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private UserMapper mapper;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private UserService service;

    @Test
    void register_Success() {
        UserDto dto = new UserDto(null, "test@email.com", "pass1234", "Test", "USER");
        when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(encoder.encode(dto.password())).thenReturn("hashed");
        User user = User.builder().id(UUID.randomUUID()).email(dto.email()).passwordHash("hashed").fullName(dto.fullName()).role(dto.role()).build();
        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(new UserDto(user.getId(), user.getEmail(), null, user.getFullName(), user.getRole()));

        UserDto result = service.register(dto);
        assertNotNull(result.id());
    }

    @Test
    void findById_NotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.findById(id));
    }

    // Add more tests
}
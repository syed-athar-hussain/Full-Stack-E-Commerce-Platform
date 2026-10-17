/*
 * package backend.user.service;
 * 
 * import backend.shared.dto.UserDto; import backend.shared.util.JwtUtil; import
 * backend.user.exception.UserNotFoundException; import
 * backend.user.mapper.UserMapper; import backend.user.model.User; import
 * backend.user.repository.UserRepository; import
 * io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; import
 * io.github.resilience4j.retry.annotation.Retry; import
 * lombok.RequiredArgsConstructor; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory; import
 * org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import
 * org.springframework.stereotype.Service; import
 * org.springframework.transaction.annotation.Transactional;
 * 
 * import java.util.UUID;
 * 
 * @Service
 * 
 * @RequiredArgsConstructor public class UserService { private static final
 * Logger log = LoggerFactory.getLogger(UserService.class); private final
 * UserRepository repository; private final BCryptPasswordEncoder encoder;
 * private final UserMapper mapper; private final JwtUtil jwtUtil;
 * 
 * @Transactional
 * 
 * @CircuitBreaker(name = "userService")
 * 
 * @Retry(name = "userService") public UserDto register(UserDto dto) {
 * repository.findByEmail(dto.email()).ifPresent(u -> { throw new
 * IllegalArgumentException("Email exists"); }); User user = User.builder()
 * .email(dto.email()) .passwordHash(encoder.encode(dto.password()))
 * .fullName(dto.fullName()) .role(dto.role() != null ? dto.role() : "USER")
 * .build(); User saved = repository.save(user);
 * log.info("Registered user ID: {}", saved.getId()); return
 * mapper.toDto(saved); }
 * 
 * @CircuitBreaker(name = "userService") public String login(String email,
 * String password) { User user = repository.findByEmail(email).orElseThrow(()
 * -> new UserNotFoundException("Invalid email")); if
 * (!encoder.matches(password, user.getPasswordHash())) { throw new
 * IllegalArgumentException("Invalid password"); }
 * log.info("Logged in user ID: {}", user.getId()); return
 * jwtUtil.generateToken(user.getId(), user.getRole()); }
 * 
 * @CircuitBreaker(name = "userService") public UserDto findById(UUID id) {
 * return repository.findById(id) .map(mapper::toDto) .orElseThrow(() -> new
 * UserNotFoundException(id)); }
 * 
 * @Transactional
 * 
 * @CircuitBreaker(name = "userService") public UserDto update(UUID id, UserDto
 * dto) { User user = repository.findById(id).orElseThrow(() -> new
 * UserNotFoundException(id)); if (dto.email() != null)
 * user.setEmail(dto.email()); if (dto.password() != null)
 * user.setPasswordHash(encoder.encode(dto.password())); if (dto.fullName() !=
 * null) user.setFullName(dto.fullName()); if (dto.role() != null)
 * user.setRole(dto.role()); User saved = repository.save(user);
 * log.info("Updated user ID: {}", saved.getId()); return mapper.toDto(saved); }
 * 
 * @Transactional
 * 
 * @CircuitBreaker(name = "userService") public void delete(UUID id) {
 * repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
 * repository.deleteById(id); log.info("Deleted user ID: {}", id); }
 * 
 * public UserService(UserRepository repository, BCryptPasswordEncoder encoder,
 * UserMapper mapper, JwtUtil jwtUtil) { this.repository = repository;
 * this.encoder = encoder; this.mapper = mapper; this.jwtUtil = jwtUtil; } }
 */

package backend.user.service;

import backend.shared.dto.UserDto;
import backend.shared.util.JwtUtil;
import backend.user.exception.UserNotFoundException;
import backend.user.mapper.UserMapper;
import backend.user.model.User;
import backend.user.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final UserMapper mapper;
    private final JwtUtil jwtUtil;

    @Transactional
    @CircuitBreaker(name = "userService")
    @Retry(name = "userService")
    public UserDto register(UserDto dto) {
        repository.findByEmail(dto.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already exists");
        });
		/*
		 * User user = User.builder() .email(dto.email())
		 * .passwordHash(encoder.encode(dto.password())) .fullName(dto.fullName())
		 * .role(dto.role() != null ? dto.role() : "USER") .build(); User saved =
		 * repository.save(user); log.info("Registered user: {}", saved.getId()); return
		 * mapper.toDto(saved);
		 */
        String hashedPassword = encoder.encode(dto.password());
        User user = new User(dto.email(), hashedPassword, dto.fullName(), dto.role() != null ? dto.role() : "USER");
        User saved = repository.save(user);
        log.info("User registered: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @CircuitBreaker(name = "userService")
    public String login(String email, String password) {
        User user = repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Invalid credentials"));
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    @CircuitBreaker(name = "userService")
    public UserDto findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    @CircuitBreaker(name = "userService")
    public UserDto update(UUID id, UserDto dto) {
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPasswordHash(encoder.encode(dto.password()));
        if (dto.fullName() != null) user.setFullName(dto.fullName());
        if (dto.role() != null) user.setRole(dto.role());
        User saved = repository.save(user);
        log.info("Updated user: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Transactional
    @CircuitBreaker(name = "userService")
    public void delete(UUID id) {
        repository.deleteById(id);
        log.info("Deleted user: {}", id);
    }
}
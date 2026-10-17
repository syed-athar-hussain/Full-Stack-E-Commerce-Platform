package backend.user.controller;


import backend.shared.dto.UserDto;
import backend.user.dto.LoginDto;
import backend.user.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Timed("user.api")
public class UserController {
    private final UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(service.login(dto.email(), dto.password()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}

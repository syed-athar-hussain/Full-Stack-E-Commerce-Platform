package backend.user.service;

import backend.user.model.User;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID id;
        try {
            id = UUID.fromString(username);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid ID format");
        }
        return repository.findById(id)
                .map(this::buildUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getId().toString())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .build();
    }
    
}

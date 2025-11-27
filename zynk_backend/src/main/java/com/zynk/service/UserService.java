package com.zynk.service;

import com.zynk.dto.AuthResponse;
import com.zynk.dto.LoginRequest;
import com.zynk.entity.User;
import com.zynk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public Optional<AuthResponse> login(LoginRequest request, User.UserRole expectedRole) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if user role matches expected role
            if (user.getRole() != expectedRole) {
                return Optional.empty();
            }
            
            // Verify password
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name());
                return Optional.of(new AuthResponse(
                    token,
                    user.getEmail(),
                    user.getName(),
                    user.getRole(),
                    user.getId()
                ));
            }
        }
        
        return Optional.empty();
    }
    
    public User createUser(String email, String password, String name, User.UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}


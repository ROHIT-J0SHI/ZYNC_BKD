package com.zynk.controller;

import com.zynk.dto.LoginRequest;
import com.zynk.entity.User;
import com.zynk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login/hr")
    public ResponseEntity<?> loginHR(@Valid @RequestBody LoginRequest request) {
        return userService.login(request, User.UserRole.HR)
            .map(authResponse -> ResponseEntity.ok((Object) authResponse))
            .orElse(ResponseEntity.status(401).body("Invalid HR credentials"));
    }
    
    @PostMapping("/login/intern")
    public ResponseEntity<?> loginIntern(@Valid @RequestBody LoginRequest request) {
        return userService.login(request, User.UserRole.INTERN)
            .map(authResponse -> ResponseEntity.ok((Object) authResponse))
            .orElse(ResponseEntity.status(401).body("Invalid intern credentials"));
    }
}


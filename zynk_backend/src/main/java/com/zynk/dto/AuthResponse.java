package com.zynk.dto;

import com.zynk.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private User.UserRole role;
    private Long userId;
}


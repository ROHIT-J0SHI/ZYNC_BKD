package com.zynk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HrCreateInternRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid manager email format")
    private String managerEmail; // Optional
    
    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
    
    @NotNull(message = "Internship duration is required")
    @Min(value = 3, message = "Duration must be 3 or 6 months")
    @Max(value = 6, message = "Duration must be 3 or 6 months")
    private Integer internshipDurationMonths;
    
    @NotNull(message = "Stipend amount is required")
    @Positive(message = "Stipend amount must be positive")
    private Double stipendAmount;
}


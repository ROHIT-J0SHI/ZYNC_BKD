package com.zynk.dto;

import com.zynk.entity.InternDetails;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InternUpdateDetailsRequest {
    private LocalDate joiningDate;
    
    @Min(value = 3, message = "Duration must be 3 or 6 months")
    @Max(value = 6, message = "Duration must be 3 or 6 months")
    private Integer internshipDurationMonths;
    
    private InternDetails.StipendType stipendType;
    
    @Positive(message = "Stipend amount must be positive")
    private Double stipendAmount;
    
    private String panNumber;
    private String aadhaarNumber;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String bankBranch;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phoneNumber;
}


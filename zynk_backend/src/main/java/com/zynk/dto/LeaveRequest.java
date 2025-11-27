package com.zynk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequest {
    @NotNull(message = "Leave date is required")
    private LocalDate leaveDate;
    
    @NotNull(message = "Reason is required")
    private String reason;
}


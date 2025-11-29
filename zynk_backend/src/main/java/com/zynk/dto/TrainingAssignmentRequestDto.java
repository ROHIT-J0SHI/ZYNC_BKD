package com.zynk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TrainingAssignmentRequestDto {
    @NotNull(message = "Training ID is required")
    private Long trainingId;
    
    @NotNull(message = "Intern ID is required")
    private Long internId;
}



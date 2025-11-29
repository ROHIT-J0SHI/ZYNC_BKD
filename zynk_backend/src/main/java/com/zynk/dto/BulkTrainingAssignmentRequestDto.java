package com.zynk.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkTrainingAssignmentRequestDto {
    @NotNull(message = "Training ID is required")
    private Long trainingId;
    
    @NotEmpty(message = "At least one intern ID is required")
    private List<Long> internIds;
}



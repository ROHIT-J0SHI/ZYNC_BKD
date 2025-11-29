package com.zynk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrainingCreateRequestDto {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Link is required")
    private String link;
}



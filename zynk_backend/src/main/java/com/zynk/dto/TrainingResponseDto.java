package com.zynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TrainingResponseDto {
    private Long id;
    private String title;
    private String description;
    private String link;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



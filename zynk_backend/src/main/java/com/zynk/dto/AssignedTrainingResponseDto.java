package com.zynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssignedTrainingResponseDto {
    private Long assignmentId;
    private Long trainingId;
    private String title;
    private String description;
    private String link;
    private Long internId;
    private String internName;
    private String internEmail;
    private LocalDateTime assignedAt;
    private Boolean completed;
    private LocalDateTime completedAt;
}



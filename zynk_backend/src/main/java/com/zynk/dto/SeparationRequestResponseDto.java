package com.zynk.dto;

import com.zynk.entity.SeparationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SeparationRequestResponseDto {
    private Long id;
    private Long internId;
    private String internName;
    private String internEmail;
    private LocalDate requestedSeparationDate;
    private String reason;
    private SeparationRequest.SeparationStatus status;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String hrRemarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


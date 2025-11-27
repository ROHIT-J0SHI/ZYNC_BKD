package com.zynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String body;
    private LocalDate expiryDate;
    private String createdByName;
    private LocalDateTime createdAt;
    private Boolean isValid;
}


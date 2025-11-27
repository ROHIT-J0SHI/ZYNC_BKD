package com.zynk.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiPolicyBuddyRequest {
    @NotBlank(message = "Question is required")
    private String question;
}


package com.zynk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceGenerationRequest {
    @NotNull(message = "Month is required (1-12)")
    private Integer month;
    
    @NotNull(message = "Year is required")
    private Integer year;
}


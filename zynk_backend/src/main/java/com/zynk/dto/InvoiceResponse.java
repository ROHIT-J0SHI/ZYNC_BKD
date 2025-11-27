package com.zynk.dto;

import com.zynk.entity.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate billingPeriodFrom;
    private LocalDate billingPeriodTill;
    private Integer totalWorkingDays;
    private Integer paidLeaves;
    private Integer unpaidLeaves;
    private Double stipendAmount;
    private Invoice.InvoiceStatus status;
    private String remarks;
    private String internName;
    private String internEmail;
}


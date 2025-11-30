package com.zynk.dto;

import com.zynk.entity.Leave;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LeaveResponse {
    private Long id;
    private Long internId;
    private String internName;
    private String internEmail;
    private LocalDate leaveDate;
    private String reason;
    private Leave.LeaveStatus status;
    private Leave.LeaveType leaveType;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}


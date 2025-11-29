package com.zynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveTotalResponse {
    private Long totalLeaves;
    private Long pendingLeaves;
    private Long approvedLeaves;
    private Long rejectedLeaves;
    private Long paidLeaves;
    private Long unpaidLeaves;
}


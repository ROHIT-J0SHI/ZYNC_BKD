package com.zynk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveBalanceResponse {
    private Integer paidLeavesUsed;
    private Integer paidLeavesRemaining;
    private Integer unpaidLeavesTotal;
    private Integer totalPaidLeavesEarned;
}


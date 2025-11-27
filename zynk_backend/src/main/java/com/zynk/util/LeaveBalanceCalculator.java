package com.zynk.util;

import com.zynk.entity.InternDetails;
import com.zynk.entity.Leave;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LeaveBalanceCalculator {
    
    private static final int PAID_LEAVES_PER_MONTH = 1;
    
    /**
     * Calculates total paid leaves earned based on internship duration
     */
    public static int calculateTotalPaidLeavesEarned(InternDetails intern) {
        long monthsWorked = ChronoUnit.MONTHS.between(
            intern.getJoiningDate(), 
            LocalDate.now()
        ) + 1; // +1 because they get leave for the current month too
        
        // Cap at internship duration
        if (monthsWorked > intern.getInternshipDurationMonths()) {
            monthsWorked = intern.getInternshipDurationMonths();
        }
        
        return (int) monthsWorked * PAID_LEAVES_PER_MONTH;
    }
    
    /**
     * Counts paid leaves used from approved leaves
     */
    public static int countPaidLeavesUsed(List<Leave> approvedLeaves) {
        return (int) approvedLeaves.stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.APPROVED 
                          && leave.getLeaveType() == Leave.LeaveType.PAID)
            .count();
    }
    
    /**
     * Counts unpaid leaves from approved leaves
     */
    public static int countUnpaidLeaves(List<Leave> approvedLeaves) {
        return (int) approvedLeaves.stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.APPROVED 
                          && leave.getLeaveType() == Leave.LeaveType.UNPAID)
            .count();
    }
    
    /**
     * Determines if a leave should be PAID or UNPAID based on balance
     */
    public static Leave.LeaveType determineLeaveType(InternDetails intern, List<Leave> approvedLeaves) {
        int totalEarned = calculateTotalPaidLeavesEarned(intern);
        int used = countPaidLeavesUsed(approvedLeaves);
        int remaining = totalEarned - used;
        
        return remaining > 0 ? Leave.LeaveType.PAID : Leave.LeaveType.UNPAID;
    }
}


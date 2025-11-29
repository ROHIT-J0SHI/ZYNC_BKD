package com.zynk.service;

import com.zynk.dto.LeaveBalanceResponse;
import com.zynk.dto.LeaveRequest;
import com.zynk.dto.LeaveResponse;
import com.zynk.dto.LeaveTotalResponse;
import com.zynk.entity.InternDetails;
import com.zynk.entity.Leave;
import com.zynk.entity.User;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.repository.LeaveRepository;
import com.zynk.util.LeaveBalanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {
    
    private final LeaveRepository leaveRepository;
    private final InternDetailsRepository internDetailsRepository;
    
    @Transactional
    public Leave createLeaveRequest(Long internId, LeaveRequest request) {
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        // Check if leave date is within internship period
        if (request.getLeaveDate().isBefore(intern.getJoiningDate()) || 
            request.getLeaveDate().isAfter(intern.getInternshipEndDate())) {
            throw new RuntimeException("Leave date is outside internship period");
        }
        
        // Get all approved leaves to determine leave type
        List<Leave> approvedLeaves = leaveRepository.findByInternAndStatus(
            intern, Leave.LeaveStatus.APPROVED
        );
        
        // Determine if this should be PAID or UNPAID
        Leave.LeaveType leaveType = LeaveBalanceCalculator.determineLeaveType(intern, approvedLeaves);
        
        Leave leave = new Leave();
        leave.setIntern(intern);
        leave.setLeaveDate(request.getLeaveDate());
        leave.setReason(request.getReason());
        leave.setStatus(Leave.LeaveStatus.PENDING);
        leave.setLeaveType(leaveType);
        
        return leaveRepository.save(leave);
    }
    
    @Transactional
    public Leave approveLeave(Long leaveId, String approvedBy) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave not found"));
        
        InternDetails intern = leave.getIntern();
        
        // Recalculate leave type based on current balance
        List<Leave> approvedLeaves = leaveRepository.findByInternAndStatus(
            intern, Leave.LeaveStatus.APPROVED
        );
        Leave.LeaveType leaveType = LeaveBalanceCalculator.determineLeaveType(intern, approvedLeaves);
        
        leave.setStatus(Leave.LeaveStatus.APPROVED);
        leave.setLeaveType(leaveType);
        leave.setApprovedBy(approvedBy);
        leave.setApprovedAt(java.time.LocalDateTime.now());
        
        return leaveRepository.save(leave);
    }
    
    @Transactional
    public Leave rejectLeave(Long leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus(Leave.LeaveStatus.REJECTED);
        return leaveRepository.save(leave);
    }
    
    public List<LeaveResponse> getLeavesByIntern(Long internId) {
        List<Leave> leaves = leaveRepository.findByInternId(internId);
        return leaves.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<LeaveResponse> getAllPendingLeaves() {
        List<Leave> leaves = leaveRepository.findAll().stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.PENDING)
            .collect(Collectors.toList());
        return leaves.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<LeaveResponse> getAllApprovedLeaves() {
        List<Leave> leaves = leaveRepository.findAll().stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.APPROVED)
            .collect(Collectors.toList());
        return leaves.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<LeaveResponse> getAllRejectedLeaves() {
        List<Leave> leaves = leaveRepository.findAll().stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.REJECTED)
            .collect(Collectors.toList());
        return leaves.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<LeaveResponse> getAllPaidLeaves() {
        List<Leave> leaves = leaveRepository.findAll().stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.PAID)
            .collect(Collectors.toList());
        return leaves.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<LeaveResponse> getAllUnpaidLeaves() {
        List<Leave> leaves = leaveRepository.findAll().stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.UNPAID)
            .collect(Collectors.toList());
        return leaves.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public LeaveBalanceResponse getLeaveBalance(Long internId) {
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        List<Leave> approvedLeaves = leaveRepository.findByInternAndStatus(
            intern, Leave.LeaveStatus.APPROVED
        );
        
        int totalEarned = LeaveBalanceCalculator.calculateTotalPaidLeavesEarned(intern);
        int used = LeaveBalanceCalculator.countPaidLeavesUsed(approvedLeaves);
        int remaining = totalEarned - used;
        int unpaid = LeaveBalanceCalculator.countUnpaidLeaves(approvedLeaves);
        
        return new LeaveBalanceResponse(used, remaining, unpaid, totalEarned);
    }
    
    public LeaveTotalResponse getTotalLeaves() {
        List<Leave> allLeaves = leaveRepository.findAll();
        
        long totalLeaves = allLeaves.size();
        long pendingLeaves = allLeaves.stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.PENDING)
            .count();
        long approvedLeaves = allLeaves.stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.APPROVED)
            .count();
        long rejectedLeaves = allLeaves.stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.REJECTED)
            .count();
        long paidLeaves = allLeaves.stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.PAID)
            .count();
        long unpaidLeaves = allLeaves.stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.UNPAID)
            .count();
        
        return new LeaveTotalResponse(
            totalLeaves,
            pendingLeaves,
            approvedLeaves,
            rejectedLeaves,
            paidLeaves,
            unpaidLeaves
        );
    }
    
    private LeaveResponse toResponse(Leave leave) {
        return new LeaveResponse(
            leave.getId(),
            leave.getLeaveDate(),
            leave.getReason(),
            leave.getStatus(),
            leave.getLeaveType(),
            leave.getApprovedBy(),
            leave.getApprovedAt(),
            leave.getCreatedAt()
        );
    }
}


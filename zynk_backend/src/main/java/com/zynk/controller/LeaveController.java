package com.zynk.controller;

import com.zynk.dto.LeaveBalanceResponse;
import com.zynk.dto.LeaveRequest;
import com.zynk.dto.LeaveResponse;
import com.zynk.entity.Leave;
import com.zynk.service.InternDetailsService;
import com.zynk.service.LeaveService;
import com.zynk.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {
    
    private final LeaveService leaveService;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    
    @PostMapping("/request")
    public ResponseEntity<?> requestLeave(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody LeaveRequest request) {
        try {
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
            Leave leave = leaveService.createLeaveRequest(internId, request);
            return ResponseEntity.ok(leave);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/my-leaves")
    public ResponseEntity<List<LeaveResponse>> getMyLeaves(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
        return ResponseEntity.ok(leaveService.getLeavesByIntern(internId));
    }
    
    @GetMapping("/balance")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalance(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
        return ResponseEntity.ok(leaveService.getLeaveBalance(internId));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getAllPendingLeaves());
    }
    
    @PutMapping("/{leaveId}/approve")
    public ResponseEntity<?> approveLeave(
            @PathVariable Long leaveId,
            @RequestParam String approvedBy) {
        try {
            Leave leave = leaveService.approveLeave(leaveId, approvedBy);
            return ResponseEntity.ok(leave);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{leaveId}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long leaveId) {
        try {
            Leave leave = leaveService.rejectLeave(leaveId);
            return ResponseEntity.ok(leave);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


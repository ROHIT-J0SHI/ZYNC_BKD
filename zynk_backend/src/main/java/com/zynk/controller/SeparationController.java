package com.zynk.controller;

import com.zynk.dto.SeparationRequestCreateDto;
import com.zynk.dto.SeparationRequestResponseDto;
import com.zynk.service.InternDetailsService;
import com.zynk.service.JwtService;
import com.zynk.service.SeparationService;
import com.zynk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/separations")
@RequiredArgsConstructor
public class SeparationController {
    
    private final SeparationService separationService;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    private final UserService userService;
    
    @PostMapping("/request")
    public ResponseEntity<?> createSeparationRequest(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SeparationRequestCreateDto request) {
        try {
            // Verify INTERN role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"INTERN".equals(role)) {
                return ResponseEntity.status(403).body("Only interns can create separation requests");
            }
            
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
            
            SeparationRequestResponseDto response = separationService.createSeparationRequest(internId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/my-requests")
    public ResponseEntity<?> getMySeparationRequests(
            @RequestHeader("Authorization") String token) {
        try {
            // Verify INTERN role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"INTERN".equals(role)) {
                return ResponseEntity.status(403).body("Only interns can view their separation requests");
            }
            
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
            
            List<SeparationRequestResponseDto> requests = separationService.getSeparationRequestsByIntern(internId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllSeparationRequests(
            @RequestHeader("Authorization") String token) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can view all separation requests");
            }
            
            List<SeparationRequestResponseDto> requests = separationService.getAllSeparationRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingSeparationRequests(
            @RequestHeader("Authorization") String token) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can view pending separation requests");
            }
            
            List<SeparationRequestResponseDto> requests = separationService.getPendingSeparationRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approveSeparationRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable Long requestId,
            @RequestParam String approvedBy) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can approve separation requests");
            }
            
            SeparationRequestResponseDto response = separationService.approveSeparationRequest(requestId, approvedBy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectSeparationRequest(
            @RequestHeader("Authorization") String token,
            @PathVariable Long requestId,
            @RequestParam(required = false) String approvedBy,
            @RequestParam(required = false) String hrRemarks) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can reject separation requests");
            }
            
            // If approvedBy is not provided, get it from the token
            if (approvedBy == null || approvedBy.trim().isEmpty()) {
                Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
                approvedBy = userService.findById(userId)
                    .map(user -> user.getName())
                    .orElse("HR");
            }
            
            SeparationRequestResponseDto response = separationService.rejectSeparationRequest(requestId, approvedBy, hrRemarks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


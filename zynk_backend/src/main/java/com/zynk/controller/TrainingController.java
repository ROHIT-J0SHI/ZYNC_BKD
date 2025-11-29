package com.zynk.controller;

import com.zynk.dto.AssignedTrainingResponseDto;
import com.zynk.dto.BulkTrainingAssignmentRequestDto;
import com.zynk.dto.TrainingAssignmentRequestDto;
import com.zynk.dto.TrainingCreateRequestDto;
import com.zynk.dto.TrainingResponseDto;
import com.zynk.dto.TrainingUpdateRequestDto;
import com.zynk.service.InternDetailsService;
import com.zynk.service.JwtService;
import com.zynk.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {
    
    private final TrainingService trainingService;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    
    // HR endpoints for training management
    @PostMapping
    public ResponseEntity<?> createTraining(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TrainingCreateRequestDto request) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can create trainings"));
            }
            
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            TrainingResponseDto response = trainingService.createTraining(request, hrUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{trainingId:\\d+}")
    public ResponseEntity<?> updateTraining(
            @RequestHeader("Authorization") String token,
            @PathVariable Long trainingId,
            @Valid @RequestBody TrainingUpdateRequestDto request) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can update trainings"));
            }
            
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            TrainingResponseDto response = trainingService.updateTraining(trainingId, request, hrUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllTrainings(@RequestHeader("Authorization") String token) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can view all trainings"));
            }
            
            List<TrainingResponseDto> trainings = trainingService.getAllTrainings();
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{trainingId:\\d+}")
    public ResponseEntity<?> getTrainingById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long trainingId) {
        try {
            TrainingResponseDto training = trainingService.getTrainingById(trainingId);
            return ResponseEntity.ok(training);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{trainingId:\\d+}")
    public ResponseEntity<?> deleteTraining(
            @RequestHeader("Authorization") String token,
            @PathVariable Long trainingId) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can delete trainings"));
            }
            
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            trainingService.deleteTraining(trainingId, hrUserId);
            return ResponseEntity.ok(Map.of("message", "Training deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // HR endpoints for assignment management
    @PostMapping("/assign")
    public ResponseEntity<?> assignTraining(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TrainingAssignmentRequestDto request) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can assign trainings"));
            }
            
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            AssignedTrainingResponseDto response = trainingService.assignTrainingToIntern(
                request.getTrainingId(), request.getInternId(), hrUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/assign/bulk")
    public ResponseEntity<?> assignTrainingToMultipleInterns(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody BulkTrainingAssignmentRequestDto request) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can assign trainings"));
            }
            
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            List<AssignedTrainingResponseDto> responses = trainingService.assignTrainingToMultipleInterns(
                request.getTrainingId(), request.getInternIds(), hrUserId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/assignments/all")
    public ResponseEntity<?> getAllAssignments(@RequestHeader("Authorization") String token) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can view all assignments"));
            }
            
            List<AssignedTrainingResponseDto> assignments = trainingService.getAllAssignmentsForHr();
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/assign")
    public ResponseEntity<?> unassignTraining(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TrainingAssignmentRequestDto request) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only HR can unassign trainings"));
            }
            
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            trainingService.unassignTrainingFromIntern(
                request.getTrainingId(), request.getInternId(), hrUserId);
            return ResponseEntity.ok(Map.of("message", "Training unassigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // INTERN endpoints
    @GetMapping("/my-assignments")
    public ResponseEntity<?> getMyAssignments(@RequestHeader("Authorization") String token) {
        try {
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            if (!"INTERN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Only interns can view their assignments"));
            }
            
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
            List<AssignedTrainingResponseDto> assignments = trainingService.getAssignmentsForIntern(internId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


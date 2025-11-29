package com.zynk.service;

import com.zynk.dto.AssignedTrainingResponseDto;
import com.zynk.dto.TrainingCreateRequestDto;
import com.zynk.dto.TrainingResponseDto;
import com.zynk.dto.TrainingUpdateRequestDto;
import com.zynk.entity.InternDetails;
import com.zynk.entity.Training;
import com.zynk.entity.TrainingAssignment;
import com.zynk.entity.User;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.repository.TrainingAssignmentRepository;
import com.zynk.repository.TrainingRepository;
import com.zynk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {
    
    private final TrainingRepository trainingRepository;
    private final TrainingAssignmentRepository trainingAssignmentRepository;
    private final UserRepository userRepository;
    private final InternDetailsRepository internDetailsRepository;
    
    @Transactional
    public TrainingResponseDto createTraining(TrainingCreateRequestDto dto, Long hrUserId) {
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can create trainings");
        }
        
        Training training = new Training();
        training.setTitle(dto.getTitle());
        training.setDescription(dto.getDescription());
        training.setLink(dto.getLink());
        training.setCreatedBy(hrUser);
        
        training = trainingRepository.save(training);
        return toTrainingResponse(training);
    }
    
    @Transactional
    public TrainingResponseDto updateTraining(Long id, TrainingUpdateRequestDto dto, Long hrUserId) {
        Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Training not found"));
        
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can update trainings");
        }
        
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            training.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            training.setDescription(dto.getDescription());
        }
        if (dto.getLink() != null && !dto.getLink().trim().isEmpty()) {
            training.setLink(dto.getLink());
        }
        
        training = trainingRepository.save(training);
        return toTrainingResponse(training);
    }
    
    @Transactional
    public void deleteTraining(Long id, Long hrUserId) {
        Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Training not found"));
        
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can delete trainings");
        }
        
        // Check if training has any assignments
        List<TrainingAssignment> assignments = trainingAssignmentRepository.findByTraining(training);
        if (!assignments.isEmpty()) {
            throw new RuntimeException("Cannot delete training that has been assigned to interns");
        }
        
        trainingRepository.delete(training);
    }
    
    public List<TrainingResponseDto> getAllTrainings() {
        return trainingRepository.findAll().stream()
            .map(this::toTrainingResponse)
            .collect(Collectors.toList());
    }
    
    public TrainingResponseDto getTrainingById(Long id) {
        Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Training not found"));
        return toTrainingResponse(training);
    }
    
    @Transactional
    public AssignedTrainingResponseDto assignTrainingToIntern(Long trainingId, Long internId, Long hrUserId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new RuntimeException("Training not found"));
        
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can assign trainings");
        }
        
        // Check if assignment already exists
        trainingAssignmentRepository.findByInternAndTraining(intern, training)
            .ifPresent(existing -> {
                throw new RuntimeException("Training already assigned to this intern");
            });
        
        TrainingAssignment assignment = new TrainingAssignment();
        assignment.setTraining(training);
        assignment.setIntern(intern);
        assignment.setAssignedAt(java.time.LocalDateTime.now());
        assignment.setCompleted(false);
        
        assignment = trainingAssignmentRepository.save(assignment);
        return toAssignedTrainingResponse(assignment);
    }
    
    @Transactional
    public List<AssignedTrainingResponseDto> assignTrainingToMultipleInterns(Long trainingId, List<Long> internIds, Long hrUserId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new RuntimeException("Training not found"));
        
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can assign trainings");
        }
        
        return internIds.stream()
            .map(internId -> {
                try {
                    return assignTrainingToIntern(trainingId, internId, hrUserId);
                } catch (RuntimeException e) {
                    // Skip if already assigned
                    if (e.getMessage().contains("already assigned")) {
                        return null;
                    }
                    throw e;
                }
            })
            .filter(response -> response != null)
            .collect(Collectors.toList());
    }
    
    public List<AssignedTrainingResponseDto> getAssignmentsForIntern(Long internId) {
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        return trainingAssignmentRepository.findByIntern(intern).stream()
            .map(this::toAssignedTrainingResponse)
            .collect(Collectors.toList());
    }
    
    public List<AssignedTrainingResponseDto> getAllAssignmentsForHr() {
        return trainingAssignmentRepository.findAll().stream()
            .map(this::toAssignedTrainingResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void unassignTrainingFromIntern(Long trainingId, Long internId, Long hrUserId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new RuntimeException("Training not found"));
        
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can unassign trainings");
        }
        
        TrainingAssignment assignment = trainingAssignmentRepository.findByInternAndTraining(intern, training)
            .orElseThrow(() -> new RuntimeException("Training assignment not found"));
        
        trainingAssignmentRepository.delete(assignment);
    }
    
    private TrainingResponseDto toTrainingResponse(Training training) {
        return new TrainingResponseDto(
            training.getId(),
            training.getTitle(),
            training.getDescription(),
            training.getLink(),
            training.getCreatedAt(),
            training.getUpdatedAt()
        );
    }
    
    private AssignedTrainingResponseDto toAssignedTrainingResponse(TrainingAssignment assignment) {
        Training training = assignment.getTraining();
        InternDetails intern = assignment.getIntern();
        User internUser = intern.getUser();
        
        return new AssignedTrainingResponseDto(
            assignment.getId(),
            training.getId(),
            training.getTitle(),
            training.getDescription(),
            training.getLink(),
            intern.getId(),
            internUser != null ? internUser.getName() : null,
            internUser != null ? internUser.getEmail() : null,
            assignment.getAssignedAt(),
            assignment.getCompleted(),
            assignment.getCompletedAt()
        );
    }
}



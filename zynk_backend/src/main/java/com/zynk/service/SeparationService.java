package com.zynk.service;

import com.zynk.dto.SeparationRequestCreateDto;
import com.zynk.dto.SeparationRequestResponseDto;
import com.zynk.entity.InternDetails;
import com.zynk.entity.SeparationRequest;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.repository.SeparationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeparationService {
    
    private final SeparationRequestRepository separationRequestRepository;
    private final InternDetailsRepository internDetailsRepository;
    
    @Transactional
    public SeparationRequestResponseDto createSeparationRequest(Long internId, SeparationRequestCreateDto request) {
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        // Check if there's already a PENDING request
        separationRequestRepository.findByInternAndStatus(
            intern, SeparationRequest.SeparationStatus.PENDING
        ).ifPresent(existing -> {
            throw new RuntimeException("A pending separation request already exists");
        });
        
        // Validate requested separation date
        LocalDate today = LocalDate.now();
        LocalDate requestedDate = request.getRequestedSeparationDate();
        
        if (requestedDate.isBefore(today)) {
            throw new RuntimeException("Requested separation date cannot be in the past. Please use today's date (" + today + ") or a future date.");
        }
        
        if (intern.getJoiningDate() != null && requestedDate.isBefore(intern.getJoiningDate())) {
            throw new RuntimeException("Requested separation date cannot be before joining date");
        }
        
        // Optionally check if date is before internship end date (if internship end date exists)
        if (intern.getJoiningDate() != null && intern.getInternshipDurationMonths() != null) {
            LocalDate internshipEndDate = intern.getInternshipEndDate();
            if (internshipEndDate != null && requestedDate.isAfter(internshipEndDate)) {
                throw new RuntimeException("Requested separation date cannot be after internship end date");
            }
        }
        
        // Create separation request
        SeparationRequest separationRequest = new SeparationRequest();
        separationRequest.setIntern(intern);
        separationRequest.setRequestedSeparationDate(requestedDate);
        separationRequest.setReason(request.getReason());
        separationRequest.setStatus(SeparationRequest.SeparationStatus.PENDING);
        
        separationRequest = separationRequestRepository.save(separationRequest);
        
        return toResponse(separationRequest);
    }
    
    @Transactional
    public SeparationRequestResponseDto approveSeparationRequest(Long requestId, String approvedBy) {
        SeparationRequest request = separationRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Separation request not found"));
        
        // Check if already processed
        if (request.getStatus() != SeparationRequest.SeparationStatus.PENDING) {
            throw new RuntimeException("Separation request has already been processed");
        }
        
        request.setStatus(SeparationRequest.SeparationStatus.APPROVED);
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(java.time.LocalDateTime.now());
        
        // Update intern's status and internship end date
        InternDetails intern = request.getIntern();
        
        // Mark intern as inactive
        intern.setActive(false);
        
        // Update internship duration to reflect the requested separation date
        if (intern.getJoiningDate() != null) {
            long monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(
                intern.getJoiningDate().withDayOfMonth(1),
                request.getRequestedSeparationDate().withDayOfMonth(1)
            );
            intern.setInternshipDurationMonths((int) monthsBetween);
        }
        
        internDetailsRepository.save(intern);
        request = separationRequestRepository.save(request);
        
        return toResponse(request);
    }
    
    @Transactional
    public SeparationRequestResponseDto rejectSeparationRequest(Long requestId, String approvedBy, String hrRemarks) {
        SeparationRequest request = separationRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Separation request not found"));
        
        // Check if already processed
        if (request.getStatus() != SeparationRequest.SeparationStatus.PENDING) {
            throw new RuntimeException("Separation request has already been processed");
        }
        
        request.setStatus(SeparationRequest.SeparationStatus.REJECTED);
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(java.time.LocalDateTime.now());
        if (hrRemarks != null && !hrRemarks.trim().isEmpty()) {
            request.setHrRemarks(hrRemarks);
        }
        
        request = separationRequestRepository.save(request);
        
        return toResponse(request);
    }
    
    public List<SeparationRequestResponseDto> getSeparationRequestsByIntern(Long internId) {
        List<SeparationRequest> requests = separationRequestRepository.findByInternId(internId);
        return requests.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<SeparationRequestResponseDto> getAllSeparationRequests() {
        List<SeparationRequest> requests = separationRequestRepository.findAll();
        return requests.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<SeparationRequestResponseDto> getPendingSeparationRequests() {
        List<SeparationRequest> requests = separationRequestRepository.findByStatus(
            SeparationRequest.SeparationStatus.PENDING
        );
        return requests.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    private SeparationRequestResponseDto toResponse(SeparationRequest request) {
        InternDetails intern = request.getIntern();
        return new SeparationRequestResponseDto(
            request.getId(),
            intern.getId(),
            intern.getUser() != null ? intern.getUser().getName() : null,
            intern.getUser() != null ? intern.getUser().getEmail() : null,
            request.getRequestedSeparationDate(),
            request.getReason(),
            request.getStatus(),
            request.getApprovedBy(),
            request.getApprovedAt(),
            request.getHrRemarks(),
            request.getCreatedAt(),
            request.getUpdatedAt()
        );
    }
}


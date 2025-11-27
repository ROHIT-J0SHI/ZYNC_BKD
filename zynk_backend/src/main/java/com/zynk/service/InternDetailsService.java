package com.zynk.service;

import com.zynk.entity.InternDetails;
import com.zynk.repository.InternDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternDetailsService {
    
    private final InternDetailsRepository internDetailsRepository;
    
    /**
     * Gets InternDetails ID from User ID
     * @param userId The User ID from JWT token
     * @return InternDetails ID
     * @throws RuntimeException if intern details not found
     */
    public Long getInternDetailsIdByUserId(Long userId) {
        InternDetails internDetails = internDetailsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Intern details not found for user ID: " + userId));
        return internDetails.getId();
    }
    
    /**
     * Gets InternDetails by User ID
     */
    public InternDetails getInternDetailsByUserId(Long userId) {
        return internDetailsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Intern details not found for user ID: " + userId));
    }
}


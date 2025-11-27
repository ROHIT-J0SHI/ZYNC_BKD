package com.zynk.service;

import com.zynk.dto.AnnouncementRequest;
import com.zynk.dto.AnnouncementResponse;
import com.zynk.entity.Announcement;
import com.zynk.entity.User;
import com.zynk.repository.AnnouncementRepository;
import com.zynk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Announcement createAnnouncement(Long hrUserId, AnnouncementRequest request) {
        User hrUser = userRepository.findById(hrUserId)
            .orElseThrow(() -> new RuntimeException("HR user not found"));
        
        if (hrUser.getRole() != User.UserRole.HR) {
            throw new RuntimeException("Only HR can create announcements");
        }
        
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setBody(request.getBody());
        announcement.setExpiryDate(request.getExpiryDate());
        announcement.setCreatedBy(hrUser);
        announcement.setIsActive(true);
        
        return announcementRepository.save(announcement);
    }
    
    public List<AnnouncementResponse> getActiveAnnouncements() {
        List<Announcement> announcements = announcementRepository
            .findByIsActiveTrueAndExpiryDateGreaterThanEqual(LocalDate.now());
        
        return announcements.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void deactivateAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
            .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcement.setIsActive(false);
        announcementRepository.save(announcement);
    }
    
    private AnnouncementResponse toResponse(Announcement announcement) {
        return new AnnouncementResponse(
            announcement.getId(),
            announcement.getTitle(),
            announcement.getBody(),
            announcement.getExpiryDate(),
            announcement.getCreatedBy().getName(),
            announcement.getCreatedAt(),
            announcement.isValid()
        );
    }
}


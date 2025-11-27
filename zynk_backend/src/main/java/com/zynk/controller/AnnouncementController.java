package com.zynk.controller;

import com.zynk.dto.AnnouncementRequest;
import com.zynk.dto.AnnouncementResponse;
import com.zynk.service.AnnouncementService;
import com.zynk.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    private final JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<?> createAnnouncement(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AnnouncementRequest request) {
        try {
            Long hrUserId = jwtService.extractUserId(token.replace("Bearer ", ""));
            return ResponseEntity.ok(announcementService.createAnnouncement(hrUserId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementResponse>> getActiveAnnouncements() {
        return ResponseEntity.ok(announcementService.getActiveAnnouncements());
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }
    
    @PutMapping("/{announcementId}/deactivate")
    public ResponseEntity<?> deactivateAnnouncement(@PathVariable Long announcementId) {
        try {
            announcementService.deactivateAnnouncement(announcementId);
            return ResponseEntity.ok("Announcement deactivated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


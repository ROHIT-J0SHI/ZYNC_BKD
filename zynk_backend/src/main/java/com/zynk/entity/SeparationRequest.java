package com.zynk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "separation_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeparationRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private InternDetails intern;
    
    @Column(nullable = false)
    private LocalDate requestedSeparationDate;
    
    @Column(nullable = false, length = 1000)
    private String reason;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeparationStatus status; // PENDING, APPROVED, REJECTED
    
    private String approvedBy; // HR name/identifier
    private LocalDateTime approvedAt;
    
    @Column(length = 1000)
    private String hrRemarks; // Optional HR comments
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = SeparationStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum SeparationStatus {
        PENDING, APPROVED, REJECTED
    }
}


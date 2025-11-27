package com.zynk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leave {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private InternDetails intern;
    
    @Column(nullable = false)
    private LocalDate leaveDate;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveStatus status; // PENDING, APPROVED, REJECTED
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; // PAID or UNPAID (determined by system)
    
    private String approvedBy; // HR/Manager name
    private LocalDateTime approvedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED
    }
    
    public enum LeaveType {
        PAID, UNPAID
    }
}


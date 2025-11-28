package com.zynk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;
    
    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private InternDetails intern;
    
    @Column(nullable = false)
    private LocalDateTime assignedAt;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
        if (completed == null) {
            completed = false;
        }
    }
}


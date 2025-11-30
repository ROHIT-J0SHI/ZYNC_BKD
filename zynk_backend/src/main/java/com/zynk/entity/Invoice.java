package com.zynk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "intern_id", nullable = false)
    private InternDetails intern;
    
    @Column(nullable = false, unique = true)
    private String invoiceNumber; // Format: 001, 002, etc.
    
    @Column(nullable = false)
    private LocalDate invoiceDate;
    
    @Column(nullable = false)
    private LocalDate billingPeriodFrom;
    
    @Column(nullable = false)
    private LocalDate billingPeriodTill;
    
    @Column(nullable = false)
    private Integer totalWorkingDays;
    
    @Column(nullable = false)
    private Integer paidLeaves;
    
    @Column(nullable = false)
    private Integer unpaidLeaves;
    
    @Column(nullable = false)
    private Double stipendAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


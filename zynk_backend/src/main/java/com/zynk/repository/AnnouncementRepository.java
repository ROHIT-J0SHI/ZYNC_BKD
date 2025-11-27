package com.zynk.repository;

import com.zynk.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByIsActiveTrue();
    List<Announcement> findByIsActiveTrueAndExpiryDateGreaterThanEqual(LocalDate date);
    List<Announcement> findByCreatedById(Long userId);
}


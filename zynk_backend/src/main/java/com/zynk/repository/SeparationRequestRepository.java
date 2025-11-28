package com.zynk.repository;

import com.zynk.entity.InternDetails;
import com.zynk.entity.SeparationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeparationRequestRepository extends JpaRepository<SeparationRequest, Long> {
    List<SeparationRequest> findByIntern(InternDetails intern);
    List<SeparationRequest> findByInternId(Long internId);
    List<SeparationRequest> findByStatus(SeparationRequest.SeparationStatus status);
    Optional<SeparationRequest> findByInternAndStatus(InternDetails intern, SeparationRequest.SeparationStatus status);
}


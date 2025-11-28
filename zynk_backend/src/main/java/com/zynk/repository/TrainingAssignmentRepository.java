package com.zynk.repository;

import com.zynk.entity.InternDetails;
import com.zynk.entity.Training;
import com.zynk.entity.TrainingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingAssignmentRepository extends JpaRepository<TrainingAssignment, Long> {
    List<TrainingAssignment> findByIntern(InternDetails intern);
    List<TrainingAssignment> findByTraining(Training training);
    Optional<TrainingAssignment> findByInternAndTraining(InternDetails intern, Training training);
    List<TrainingAssignment> findByInternId(Long internId);
}


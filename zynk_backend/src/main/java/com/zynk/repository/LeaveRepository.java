package com.zynk.repository;

import com.zynk.entity.InternDetails;
import com.zynk.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByIntern(InternDetails intern);
    List<Leave> findByInternId(Long internId);
    List<Leave> findByInternAndStatus(InternDetails intern, Leave.LeaveStatus status);
    List<Leave> findByInternAndLeaveType(InternDetails intern, Leave.LeaveType leaveType);
    List<Leave> findByInternAndLeaveDateBetween(InternDetails intern, LocalDate start, LocalDate end);
    List<Leave> findByInternAndStatusAndLeaveType(InternDetails intern, Leave.LeaveStatus status, Leave.LeaveType leaveType);
}


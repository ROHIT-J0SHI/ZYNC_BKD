package com.zynk.repository;

import com.zynk.entity.InternDetails;
import com.zynk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternDetailsRepository extends JpaRepository<InternDetails, Long> {
    Optional<InternDetails> findByUser(User user);
    Optional<InternDetails> findByUserId(Long userId);
    boolean existsByPanNumber(String panNumber);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    boolean existsByBankAccountNumber(String bankAccountNumber);
}


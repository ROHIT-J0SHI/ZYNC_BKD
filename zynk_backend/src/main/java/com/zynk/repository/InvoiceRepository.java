package com.zynk.repository;

import com.zynk.entity.Invoice;
import com.zynk.entity.InternDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByIntern(InternDetails intern);
    List<Invoice> findByInternId(Long internId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByInvoiceDateBetween(LocalDate start, LocalDate end);
    List<Invoice> findByInternAndInvoiceDateBetween(InternDetails intern, LocalDate start, LocalDate end);
}


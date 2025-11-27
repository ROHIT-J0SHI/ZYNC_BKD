package com.zynk.controller;

import com.zynk.dto.InvoiceGenerationRequest;
import com.zynk.dto.InvoiceResponse;
import com.zynk.entity.Invoice;
import com.zynk.service.InternDetailsService;
import com.zynk.service.InvoiceService;
import com.zynk.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateInvoice(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody InvoiceGenerationRequest request) {
        try {
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
            Invoice invoice = invoiceService.generateInvoice(internId, request);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/my-invoices")
    public ResponseEntity<List<InvoiceResponse>> getMyInvoices(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
        return ResponseEntity.ok(invoiceService.getInvoicesByIntern(internId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }
    
    @GetMapping(value = "/{invoiceId}/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getInvoiceHtml(@PathVariable Long invoiceId) {
        try {
            String html = invoiceService.generateInvoiceHtml(invoiceId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(html, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("<html><body><h1>Error: " + e.getMessage() + "</h1></body></html>");
        }
    }
    
    @PutMapping("/{invoiceId}/status")
    public ResponseEntity<?> updateInvoiceStatus(
            @PathVariable Long invoiceId,
            @RequestParam Invoice.InvoiceStatus status,
            @RequestParam(required = false) String remarks) {
        try {
            Invoice invoice = invoiceService.updateInvoiceStatus(invoiceId, status, remarks);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoice(@PathVariable Long invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


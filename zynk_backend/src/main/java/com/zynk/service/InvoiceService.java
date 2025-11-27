package com.zynk.service;

import com.zynk.dto.InvoiceGenerationRequest;
import com.zynk.dto.InvoiceResponse;
import com.zynk.entity.InternDetails;
import com.zynk.entity.Invoice;
import com.zynk.entity.Leave;
import com.zynk.repository.InvoiceRepository;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.repository.LeaveRepository;
import com.zynk.util.InvoiceFormatter;
import com.zynk.util.InvoiceNumberGenerator;
import com.zynk.util.LeaveBalanceCalculator;
import com.zynk.util.NumberToWordsConverter;
import com.zynk.util.WorkingDaysCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final InternDetailsRepository internDetailsRepository;
    private final LeaveRepository leaveRepository;
    
    @Transactional
    public Invoice generateInvoice(Long internId, InvoiceGenerationRequest request) {
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        // Validate month is within internship period
        YearMonth invoiceMonth = YearMonth.of(request.getYear(), request.getMonth());
        YearMonth joiningMonth = YearMonth.from(intern.getJoiningDate());
        YearMonth endMonth = YearMonth.from(intern.getInternshipEndDate());
        
        if (invoiceMonth.isBefore(joiningMonth) || invoiceMonth.isAfter(endMonth)) {
            throw new RuntimeException("Invoice month is outside internship period");
        }
        
        // Check if invoice already exists for this month
        LocalDate[] monthDates = InvoiceNumberGenerator.getMonthStartEndDates(invoiceMonth);
        List<Invoice> existingInvoices = invoiceRepository.findByInternAndInvoiceDateBetween(
            intern, monthDates[0], monthDates[1]
        );
        if (!existingInvoices.isEmpty()) {
            throw new RuntimeException("Invoice already exists for this month");
        }
        
        // Generate invoice number
        String invoiceNumber = InvoiceNumberGenerator.generateInvoiceNumber(
            intern.getJoiningDate(), invoiceMonth
        );
        
        // Get month start and end dates
        LocalDate periodFrom = monthDates[0];
        LocalDate periodTill = monthDates[1];
        
        // Calculate working days
        int totalWorkingDays = WorkingDaysCalculator.calculateWorkingDays(periodFrom, periodTill);
        
        // Get leaves for this month
        List<Leave> monthLeaves = leaveRepository.findByInternAndLeaveDateBetween(
            intern, periodFrom, periodTill
        );
        List<Leave> approvedLeaves = monthLeaves.stream()
            .filter(leave -> leave.getStatus() == Leave.LeaveStatus.APPROVED)
            .collect(Collectors.toList());
        
        // Count paid and unpaid leaves
        int paidLeaves = (int) approvedLeaves.stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.PAID)
            .count();
        int unpaidLeaves = (int) approvedLeaves.stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.UNPAID)
            .count();
        
        // Calculate stipend
        double stipendAmount = calculateStipend(intern, totalWorkingDays, paidLeaves, unpaidLeaves);
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setIntern(intern);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setBillingPeriodFrom(periodFrom);
        invoice.setBillingPeriodTill(periodTill);
        invoice.setTotalWorkingDays(totalWorkingDays);
        invoice.setPaidLeaves(paidLeaves);
        invoice.setUnpaidLeaves(unpaidLeaves);
        invoice.setStipendAmount(stipendAmount);
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        
        return invoiceRepository.save(invoice);
    }
    
    private double calculateStipend(InternDetails intern, int workingDays, int paidLeaves, int unpaidLeaves) {
        if (intern.getStipendType() == InternDetails.StipendType.MONTHLY) {
            // Monthly stipend - deduct only unpaid leaves
            double dailyRate = intern.getStipendAmount() / workingDays;
            return intern.getStipendAmount() - (unpaidLeaves * dailyRate);
        } else {
            // Daily stipend
            int payableDays = workingDays - unpaidLeaves;
            return payableDays * intern.getStipendAmount();
        }
    }
    
    public List<InvoiceResponse> getInvoicesByIntern(Long internId) {
        List<Invoice> invoices = invoiceRepository.findByInternId(internId);
        return invoices.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public Invoice updateInvoiceStatus(Long invoiceId, Invoice.InvoiceStatus status, String remarks) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        invoice.setStatus(status);
        invoice.setRemarks(remarks);
        return invoiceRepository.save(invoice);
    }
    
    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
    
    public String generateInvoiceHtml(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        InternDetails intern = invoice.getIntern();
        
        // Format invoice number as 006/2025-26
        YearMonth invoiceMonth = YearMonth.from(invoice.getBillingPeriodFrom());
        String formattedInvoiceNumber = InvoiceFormatter.formatInvoiceNumber(
            invoice.getInvoiceNumber(), invoiceMonth
        );
        
        // Format dates
        String invoiceDate = InvoiceFormatter.formatDate(invoice.getInvoiceDate());
        String periodFrom = InvoiceFormatter.formatDate(invoice.getBillingPeriodFrom());
        String periodTill = InvoiceFormatter.formatDate(invoice.getBillingPeriodTill());
        
        // Format Aadhaar
        String formattedAadhaar = InvoiceFormatter.formatAadhaar(intern.getAadhaarNumber());
        
        // Amount in words
        String amountInWords = NumberToWordsConverter.convertToWords(invoice.getStipendAmount());
        
        // Build address
        StringBuilder addressBuilder = new StringBuilder();
        if (intern.getAddress() != null && !intern.getAddress().isEmpty()) {
            addressBuilder.append(intern.getAddress());
        }
        if (intern.getCity() != null && !intern.getCity().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(intern.getCity());
        }
        if (intern.getState() != null && !intern.getState().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(intern.getState());
        }
        if (intern.getPincode() != null && !intern.getPincode().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(intern.getPincode());
        }
        String fullAddress = addressBuilder.toString();
        
        // Build HTML template
        return buildInvoiceHtml(
            formattedInvoiceNumber,
            invoiceDate,
            intern.getUser().getName(),
            fullAddress,
            intern.getPhoneNumber(),
            intern.getUser().getEmail(),
            intern.getPanNumber(),
            formattedAadhaar,
            periodFrom,
            periodTill,
            invoice.getTotalWorkingDays(),
            intern.getStipendAmount(),
            invoice.getStipendAmount(),
            amountInWords,
            intern.getBankName(),
            intern.getBankAccountNumber(),
            intern.getBankIfscCode(),
            intern.getBankBranch(),
            intern.getSignatureFilePath()
        );
    }
    
    private String buildInvoiceHtml(
            String invoiceNumber, String invoiceDate, String internName, String address,
            String phoneNumber, String email, String pan, String aadhaar, 
            String periodFrom, String periodTill, int workingDays, double monthlyRate, 
            double totalAmount, String amountInWords, String bankName, String accountNumber, 
            String ifscCode, String branchName, String signaturePath) {
        
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset='UTF-8'>\n" +
            "    <title>Invoice " + invoiceNumber + "</title>\n" +
            "    <style>\n" +
            "        body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }\n" +
            "        .header { text-align: right; margin-bottom: 30px; }\n" +
            "        .invoice-title { font-size: 24px; font-weight: bold; margin-bottom: 20px; }\n" +
            "        .section { margin-bottom: 20px; }\n" +
            "        .from-to { display: flex; justify-content: space-between; margin-bottom: 30px; }\n" +
            "        .from, .to { width: 45%; }\n" +
            "        .section-title { font-weight: bold; margin-bottom: 10px; }\n" +
            "        table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n" +
            "        table th, table td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n" +
            "        table th { background-color: #f2f2f2; font-weight: bold; }\n" +
            "        .amount-words { margin: 20px 0; font-style: italic; }\n" +
            "        .bank-details { margin-top: 30px; }\n" +
            "        .signature-section { margin-top: 50px; text-align: right; }\n" +
            "        .signature-img { max-width: 200px; max-height: 100px; }\n" +
            "        @media print { body { margin: 20px; } }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class='header'>\n" +
            "        <div>Invoice No. " + invoiceNumber + "</div>\n" +
            "        <div>Dated: " + invoiceDate + "</div>\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class='from-to'>\n" +
            "        <div class='from'>\n" +
            "            <div class='section-title'>From,</div>\n" +
            "            <div><strong>Name:</strong> " + internName + "</div>\n" +
            "            <div><strong>Address:</strong> " + (address != null && !address.isEmpty() ? address : "") + "</div>\n" +
            (phoneNumber != null && !phoneNumber.isEmpty() 
                ? "            <div><strong>Contact:</strong> " + phoneNumber + "</div>\n" 
                : "") +
            "            <div><strong>E-mail:</strong> " + email + "</div>\n" +
            "            <div><strong>PAN:</strong> " + pan + "</div>\n" +
            "            <div><strong>Aadhar:</strong> " + aadhaar + "</div>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class='to'>\n" +
            "            <div class='section-title'>To,</div>\n" +
            "            <div>FranConnect India Software Pvt. Ltd.</div>\n" +
            "            <div>17th Floor, Berger WeWork, Sector 16-B, Noida</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class='section'>\n" +
            "        <div><strong>For Software Developer Intern in Engineering Team –</strong></div>\n" +
            "        <table>\n" +
            "            <thead>\n" +
            "                <tr>\n" +
            "                    <th>From</th>\n" +
            "                    <th>Till</th>\n" +
            "                    <th>Total Working days</th>\n" +
            "                    <th>Monthly Rate</th>\n" +
            "                    <th>Total Amount</th>\n" +
            "                </tr>\n" +
            "            </thead>\n" +
            "            <tbody>\n" +
            "                <tr>\n" +
            "                    <td>" + periodFrom + "</td>\n" +
            "                    <td>" + periodTill + "</td>\n" +
            "                    <td>" + workingDays + "</td>\n" +
            "                    <td>₹" + String.format("%.2f", monthlyRate) + "</td>\n" +
            "                    <td>₹" + String.format("%.2f", totalAmount) + "</td>\n" +
            "                </tr>\n" +
            "            </tbody>\n" +
            "        </table>\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class='amount-words'>\n" +
            "        <strong>Total amount (in word):</strong> " + amountInWords + ".\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class='bank-details'>\n" +
            "        <div class='section-title'>Bank Details-</div>\n" +
            "        <div><strong>Bank Name:</strong> " + (bankName != null ? bankName : "") + "</div>\n" +
            "        <div><strong>Account Number:</strong> " + (accountNumber != null ? accountNumber : "") + "</div>\n" +
            "        <div><strong>IFSC Code:</strong> " + (ifscCode != null ? ifscCode : "") + "</div>\n" +
            "        <div><strong>Branch Name:</strong> " + (branchName != null ? branchName : "") + "</div>\n" +
            "    </div>\n" +
            "    \n" +
            "    <div class='signature-section'>\n" +
            "        <div><strong>Signature:</strong></div>\n" +
            (signaturePath != null && !signaturePath.isEmpty() 
                ? "<img src='" + signaturePath + "' alt='Signature' class='signature-img' />" 
                : "") +
            "        <div style='margin-top: 20px;'>" + internName + "</div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    }
    
    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getInvoiceDate(),
            invoice.getBillingPeriodFrom(),
            invoice.getBillingPeriodTill(),
            invoice.getTotalWorkingDays(),
            invoice.getPaidLeaves(),
            invoice.getUnpaidLeaves(),
            invoice.getStipendAmount(),
            invoice.getStatus(),
            invoice.getRemarks(),
            invoice.getIntern().getUser().getName(),
            invoice.getIntern().getUser().getEmail()
        );
    }
}


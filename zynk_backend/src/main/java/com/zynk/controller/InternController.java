package com.zynk.controller;

import com.zynk.dto.HrCreateInternRequest;
import com.zynk.dto.HrUpdateInternRequest;
import com.zynk.dto.InternUpdateDetailsRequest;
import com.zynk.entity.InternDetails;
import com.zynk.entity.User;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.service.InternDetailsService;
import com.zynk.service.JwtService;
import com.zynk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/interns")
@RequiredArgsConstructor
public class InternController {
    
    private final UserService userService;
    private final InternDetailsRepository internDetailsRepository;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    
    @PostMapping("/onboard")
    public ResponseEntity<?> onboardIntern(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody HrCreateInternRequest request) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can onboard interns");
            }
            
            // Create user
            User user = userService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                User.UserRole.INTERN
            );
            
            // Create intern details with HR-provided information
            InternDetails internDetails = new InternDetails();
            internDetails.setUser(user);
            internDetails.setManagerEmail(request.getManagerEmail()); // Optional
            internDetails.setJoiningDate(request.getJoiningDate());
            internDetails.setInternshipDurationMonths(request.getInternshipDurationMonths());
            internDetails.setStipendAmount(request.getStipendAmount());
            internDetails.setStipendType(InternDetails.StipendType.MONTHLY); // Default to MONTHLY
            
            internDetailsRepository.save(internDetails);
            
            return ResponseEntity.ok("Intern onboarded successfully. Intern can now login and fill their details.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/my-details")
    public ResponseEntity<?> updateMyDetails(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody InternUpdateDetailsRequest request) {
        try {
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Optional<InternDetails> internDetailsOpt = internDetailsRepository.findByUserId(userId);
            
            if (internDetailsOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Intern details not found");
            }
            
            InternDetails internDetails = internDetailsOpt.get();
            
            // Check for unique constraints if updating PAN/Aadhaar/Bank Account
            if (request.getPanNumber() != null && !request.getPanNumber().isEmpty()) {
                if (internDetailsRepository.existsByPanNumber(request.getPanNumber()) && 
                    (internDetails.getPanNumber() == null || !internDetails.getPanNumber().equals(request.getPanNumber()))) {
                    return ResponseEntity.badRequest().body("PAN number already exists");
                }
            }
            
            if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isEmpty()) {
                if (internDetailsRepository.existsByAadhaarNumber(request.getAadhaarNumber()) && 
                    (internDetails.getAadhaarNumber() == null || !internDetails.getAadhaarNumber().equals(request.getAadhaarNumber()))) {
                    return ResponseEntity.badRequest().body("Aadhaar number already exists");
                }
            }
            
            if (request.getBankAccountNumber() != null && !request.getBankAccountNumber().isEmpty()) {
                if (internDetailsRepository.existsByBankAccountNumber(request.getBankAccountNumber()) && 
                    (internDetails.getBankAccountNumber() == null || !internDetails.getBankAccountNumber().equals(request.getBankAccountNumber()))) {
                    return ResponseEntity.badRequest().body("Bank account number already exists");
                }
            }
            
            // Update fields if provided (only personal details, not joining date, duration, or stipend)
            if (request.getPanNumber() != null) {
                internDetails.setPanNumber(request.getPanNumber());
            }
            if (request.getAadhaarNumber() != null) {
                internDetails.setAadhaarNumber(request.getAadhaarNumber());
            }
            if (request.getBankAccountNumber() != null) {
                internDetails.setBankAccountNumber(request.getBankAccountNumber());
            }
            if (request.getBankIfscCode() != null) {
                internDetails.setBankIfscCode(request.getBankIfscCode());
            }
            if (request.getBankName() != null) {
                internDetails.setBankName(request.getBankName());
            }
            if (request.getBankBranch() != null) {
                internDetails.setBankBranch(request.getBankBranch());
            }
            if (request.getAddress() != null) {
                internDetails.setAddress(request.getAddress());
            }
            if (request.getCity() != null) {
                internDetails.setCity(request.getCity());
            }
            if (request.getState() != null) {
                internDetails.setState(request.getState());
            }
            if (request.getPincode() != null) {
                internDetails.setPincode(request.getPincode());
            }
            if (request.getPhoneNumber() != null) {
                internDetails.setPhoneNumber(request.getPhoneNumber());
            }
            
            internDetailsRepository.save(internDetails);
            
            return ResponseEntity.ok("Intern details updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{internId}")
    public ResponseEntity<?> updateInternDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long internId,
            @Valid @RequestBody HrUpdateInternRequest request) {
        try {
            // Verify HR role
            String role = jwtService.extractRole(token.replace("Bearer ", ""));
            
            if (!"HR".equals(role)) {
                return ResponseEntity.status(403).body("Only HR can update intern details");
            }
            
            Optional<InternDetails> internDetailsOpt = internDetailsRepository.findById(internId);
            
            if (internDetailsOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Intern not found");
            }
            
            InternDetails internDetails = internDetailsOpt.get();
            
            // Check for unique constraints if updating PAN/Aadhaar/Bank Account
            if (request.getPanNumber() != null && !request.getPanNumber().isEmpty()) {
                if (internDetailsRepository.existsByPanNumber(request.getPanNumber()) && 
                    (internDetails.getPanNumber() == null || !internDetails.getPanNumber().equals(request.getPanNumber()))) {
                    return ResponseEntity.badRequest().body("PAN number already exists");
                }
            }
            
            if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isEmpty()) {
                if (internDetailsRepository.existsByAadhaarNumber(request.getAadhaarNumber()) && 
                    (internDetails.getAadhaarNumber() == null || !internDetails.getAadhaarNumber().equals(request.getAadhaarNumber()))) {
                    return ResponseEntity.badRequest().body("Aadhaar number already exists");
                }
            }
            
            if (request.getBankAccountNumber() != null && !request.getBankAccountNumber().isEmpty()) {
                if (internDetailsRepository.existsByBankAccountNumber(request.getBankAccountNumber()) && 
                    (internDetails.getBankAccountNumber() == null || !internDetails.getBankAccountNumber().equals(request.getBankAccountNumber()))) {
                    return ResponseEntity.badRequest().body("Bank account number already exists");
                }
            }
            
            // Update user name if provided
            if (request.getName() != null && !request.getName().isEmpty()) {
                User user = internDetails.getUser();
                user.setName(request.getName());
                userService.updateUser(user);
            }
            
            // Update fields if provided
            if (request.getManagerEmail() != null) {
                internDetails.setManagerEmail(request.getManagerEmail());
            }
            if (request.getJoiningDate() != null) {
                internDetails.setJoiningDate(request.getJoiningDate());
            }
            if (request.getInternshipDurationMonths() != null) {
                internDetails.setInternshipDurationMonths(request.getInternshipDurationMonths());
            }
            if (request.getStipendType() != null) {
                internDetails.setStipendType(request.getStipendType());
            }
            if (request.getStipendAmount() != null) {
                internDetails.setStipendAmount(request.getStipendAmount());
            }
            if (request.getPanNumber() != null) {
                internDetails.setPanNumber(request.getPanNumber());
            }
            if (request.getAadhaarNumber() != null) {
                internDetails.setAadhaarNumber(request.getAadhaarNumber());
            }
            if (request.getBankAccountNumber() != null) {
                internDetails.setBankAccountNumber(request.getBankAccountNumber());
            }
            if (request.getBankIfscCode() != null) {
                internDetails.setBankIfscCode(request.getBankIfscCode());
            }
            if (request.getBankName() != null) {
                internDetails.setBankName(request.getBankName());
            }
            if (request.getBankBranch() != null) {
                internDetails.setBankBranch(request.getBankBranch());
            }
            if (request.getAddress() != null) {
                internDetails.setAddress(request.getAddress());
            }
            if (request.getCity() != null) {
                internDetails.setCity(request.getCity());
            }
            if (request.getState() != null) {
                internDetails.setState(request.getState());
            }
            if (request.getPincode() != null) {
                internDetails.setPincode(request.getPincode());
            }
            if (request.getPhoneNumber() != null) {
                internDetails.setPhoneNumber(request.getPhoneNumber());
            }
            
            internDetailsRepository.save(internDetails);
            
            return ResponseEntity.ok("Intern details updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/my-details")
    public ResponseEntity<?> getMyDetails(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
            Optional<InternDetails> internDetailsOpt = internDetailsRepository.findByUserId(userId);
            
            if (internDetailsOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Intern details not found");
            }
            
            return ResponseEntity.ok(internDetailsOpt.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<InternDetails>> getAllInterns() {
        List<InternDetails> interns = internDetailsRepository.findAll();
        return ResponseEntity.ok(interns);
    }
}


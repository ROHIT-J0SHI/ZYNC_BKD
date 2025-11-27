package com.zynk.controller;

import com.zynk.dto.AiPolicyBuddyRequest;
import com.zynk.dto.AiPolicyBuddyResponse;
import com.zynk.service.AiService;
import com.zynk.service.InternDetailsService;
import com.zynk.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    
    private final AiService aiService;
    private final JwtService jwtService;
    private final InternDetailsService internDetailsService;
    
    @PostMapping("/policy-buddy")
    public ResponseEntity<AiPolicyBuddyResponse> getPolicyBuddyAnswer(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AiPolicyBuddyRequest request) {
        Long userId = jwtService.extractUserId(token.replace("Bearer ", ""));
        Long internId = internDetailsService.getInternDetailsIdByUserId(userId);
        String answer = aiService.getPolicyBuddyAnswer(internId, request.getQuestion());
        return ResponseEntity.ok(new AiPolicyBuddyResponse(answer));
    }
    
    @GetMapping("/hr-summary")
    public ResponseEntity<String> getHrMonthlySummary(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        String summary = aiService.getHrMonthlySummary(month, year);
        return ResponseEntity.ok(summary);
    }
}


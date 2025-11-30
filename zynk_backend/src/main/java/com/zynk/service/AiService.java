package com.zynk.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.zynk.dto.LeaveBalanceResponse;
import com.zynk.entity.InternDetails;
import com.zynk.entity.Leave;
import com.zynk.repository.InternDetailsRepository;
import com.zynk.repository.InvoiceRepository;
import com.zynk.repository.LeaveRepository;
import com.zynk.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {
    
    private final InternDetailsRepository internDetailsRepository;
    private final InvoiceRepository invoiceRepository;
    private final LeaveRepository leaveRepository;
    private final LeaveService leaveService;
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    public String getPolicyBuddyAnswer(Long internId, String question) {
        InternDetails intern = internDetailsRepository.findById(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
        
        // Get intern's data
        LeaveBalanceResponse balance = leaveService.getLeaveBalance(internId);
        List<Leave> allLeaves = leaveRepository.findByInternId(internId);
        
        // Build context
        StringBuilder context = new StringBuilder();
        context.append("Intern Policy Information:\n");
        context.append("- Internship Start: ").append(intern.getJoiningDate()).append("\n");
        context.append("- Internship End: ").append(intern.getInternshipEndDate()).append("\n");
        context.append("- Duration: ").append(intern.getInternshipDurationMonths()).append(" months\n");
        context.append("- Stipend Type: ").append(intern.getStipendType()).append("\n");
        context.append("- Stipend Amount: ₹").append(intern.getStipendAmount()).append("\n");
        context.append("- Paid Leaves Earned: ").append(balance.getTotalPaidLeavesEarned()).append("\n");
        context.append("- Paid Leaves Used: ").append(balance.getPaidLeavesUsed()).append("\n");
        context.append("- Paid Leaves Remaining: ").append(balance.getPaidLeavesRemaining()).append("\n");
        context.append("- Unpaid Leaves: ").append(balance.getUnpaidLeavesTotal()).append("\n");
        context.append("\nPolicy Rules:\n");
        context.append("- 1 paid leave per month, can carry forward\n");
        context.append("- Unpaid leaves deduct from stipend\n");
        context.append("- Monthly stipend: unpaid leaves reduce amount proportionally\n");
        context.append("- Daily stipend: unpaid leaves reduce number of payable days\n");
        
        // Build prompt
        String systemPrompt = "You are a helpful assistant for FranConnect InternFlow. " +
            "Answer questions about internship policies, leaves, and stipends based on the provided context. " +
            "Be concise, friendly, and accurate.";
        
        String userPrompt = "Context:\n" + context.toString() + "\n\nQuestion: " + question;
        
        return callOpenAI(systemPrompt, userPrompt);
    }
    
    public String getHrMonthlySummary(Integer month, Integer year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        
        // Get data for the month
        List<com.zynk.entity.Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(start, end);
        List<InternDetails> activeInterns = internDetailsRepository.findAll();
        
        // Filter active interns for that month
        List<InternDetails> monthActiveInterns = activeInterns.stream()
            .filter(intern -> {
                LocalDate joinDate = intern.getJoiningDate();
                LocalDate endDate = intern.getInternshipEndDate();
                return !joinDate.isAfter(end) && !endDate.isBefore(start);
            })
            .toList();
        
        // Get leaves for the month
        List<Leave> monthLeaves = leaveRepository.findAll().stream()
            .filter(leave -> {
                LocalDate leaveDate = leave.getLeaveDate();
                return !leaveDate.isBefore(start) && !leaveDate.isAfter(end) 
                    && leave.getStatus() == Leave.LeaveStatus.APPROVED;
            })
            .toList();
        
        int paidLeaves = (int) monthLeaves.stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.PAID)
            .count();
        int unpaidLeaves = (int) monthLeaves.stream()
            .filter(leave -> leave.getLeaveType() == Leave.LeaveType.UNPAID)
            .count();
        
        // Since invoices no longer have status, count all invoices
        int totalInvoices = invoices.size();
        
        double totalStipend = invoices.stream()
            .mapToDouble(com.zynk.entity.Invoice::getStipendAmount)
            .sum();
        
        // Find intern with highest paid leave balance
        String maxLeaveIntern = "N/A";
        int maxLeaves = -1;
        for (InternDetails intern : monthActiveInterns) {
            LeaveBalanceResponse balance = leaveService.getLeaveBalance(intern.getId());
            if (balance.getPaidLeavesRemaining() > maxLeaves) {
                maxLeaves = balance.getPaidLeavesRemaining();
                maxLeaveIntern = intern.getUser().getName();
            }
        }
        
        // Build context for AI
        StringBuilder context = new StringBuilder();
        context.append("Monthly Data for ").append(yearMonth.toString()).append(":\n");
        context.append("- Active Interns: ").append(monthActiveInterns.size()).append("\n");
        context.append("- Total Invoices: ").append(totalInvoices).append("\n");
        context.append("- Total Payable Stipend: ₹").append(String.format("%.2f", totalStipend)).append("\n");
        context.append("- Paid Leaves Taken: ").append(paidLeaves).append("\n");
        context.append("- Unpaid Leaves Taken: ").append(unpaidLeaves).append("\n");
        context.append("- Intern with Highest Paid Leave Balance: ").append(maxLeaveIntern)
            .append(" (").append(maxLeaves).append(" days)\n");
        
        String systemPrompt = "You are an HR assistant. Generate a concise 4-6 line summary of monthly intern activity " +
            "based on the provided data. Write in a professional, informative tone.";
        
        return callOpenAI(systemPrompt, "Generate a monthly summary:\n" + context.toString());
    }
    
    private String callOpenAI(String systemPrompt, String userPrompt) {
        try {
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-openai-api-key-here")) {
                return "AI service is not configured. Please set your OpenAI API key in application.properties";
            }
            
            OpenAiService service = new OpenAiService(apiKey);
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", userPrompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(500)
                .temperature(0.7)
                .build();
            
            return service.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
        } catch (Exception e) {
            return "Error generating AI response: " + e.getMessage();
        }
    }
}


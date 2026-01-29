package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO;
import com.pm.farm_backend.Service.FarmerService.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@PreAuthorize("hasRole('FARMER')")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @PostMapping("/add")
    public ResponseEntity<FinancialTransactionDTO> addTransaction(@RequestBody FinancialTransactionDTO dto,
            java.security.Principal principal) {
        FinancialTransactionDTO created = financeService.addTransaction(principal.getName(), dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/my-transactions")
    public ResponseEntity<List<FinancialTransactionDTO>> getTransactionsByUser(java.security.Principal principal) {
        return ResponseEntity.ok(financeService.getTransactionsByUser(principal.getName()));
    }

    @GetMapping("/crop-cycle/{cropCycleId}")
    public ResponseEntity<List<FinancialTransactionDTO>> getTransactionsByCropCycle(@PathVariable Long cropCycleId,
            java.security.Principal principal) {
        return ResponseEntity.ok(financeService.getTransactionsByCropCycle(principal.getName(), cropCycleId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<FinancialTransactionDTO>> getTransactionsByOrder(@PathVariable Long orderId,
            java.security.Principal principal) {
        return ResponseEntity.ok(financeService.getTransactionsByOrder(principal.getName(), orderId));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getFinancialSummary(java.security.Principal principal) {
        return ResponseEntity.ok(financeService.getFinancialSummary(principal.getName()));
    }

    @GetMapping("/trend")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getMonthlyTrend(java.security.Principal principal,
            @RequestParam(defaultValue = "0") int year) {
        if (year == 0) {
            year = java.time.LocalDate.now().getYear();
        }
        return ResponseEntity.ok(financeService.getMonthlyTrend(principal.getName(), year));
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlySummary(java.security.Principal principal,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(financeService.getMonthlySummary(principal.getName(), month, year));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<FinancialTransactionDTO>> getRecentTransactions(
            java.security.Principal principal,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(financeService.getRecentTransactions(principal.getName(), type, limit));
    }
}

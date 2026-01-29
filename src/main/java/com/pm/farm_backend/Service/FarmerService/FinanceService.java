package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FinanceService {

    FinancialTransactionDTO addTransaction(String email, FinancialTransactionDTO dto);

    List<FinancialTransactionDTO> getTransactionsByUser(String email);

    List<FinancialTransactionDTO> getTransactionsByCropCycle(String email, Long cropCycleId);

    List<FinancialTransactionDTO> getTransactionsByOrder(String email, Long orderId);

    // Returns a summary map with keys "Total Income", "Total Expense", "Net
    // Profit", "Status"
    Map<String, Object> getFinancialSummary(String email);

    // Returns a map of Month Name -> Map of Type (Income/Expense) -> Amount
    // e.g. "JANUARY" -> {"INCOME": 1000, "EXPENSE": 500}
    Map<String, Map<String, BigDecimal>> getMonthlyTrend(String email, int year);

    // Returns a detailed summary for a specific month
    Map<String, BigDecimal> getMonthlySummary(String email, int month, int year);

    // Get recent N transactions, optionally filtered by type (null/ALL for all)
    List<FinancialTransactionDTO> getRecentTransactions(String email, String type, int limit);
}

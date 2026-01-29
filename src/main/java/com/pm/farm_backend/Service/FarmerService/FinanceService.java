package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FinanceService {

    FinancialTransactionDTO addTransaction(FinancialTransactionDTO dto);

    List<FinancialTransactionDTO> getTransactionsByUser(Long userId);

    List<FinancialTransactionDTO> getTransactionsByCropCycle(Long cropCycleId);

    List<FinancialTransactionDTO> getTransactionsByOrder(Long orderId);

    // Returns a summary map with keys "Total Income", "Total Expense", "Net
    // Profit", "Status"
    Map<String, Object> getFinancialSummary(Long userId);

    // Returns a map of Month Name -> Map of Type (Income/Expense) -> Amount
    // e.g. "JANUARY" -> {"INCOME": 1000, "EXPENSE": 500}
    Map<String, Map<String, BigDecimal>> getMonthlyTrend(Long userId, int year);

    // Returns a detailed summary for a specific month
    Map<String, BigDecimal> getMonthlySummary(Long userId, int month, int year);

    // Get recent N transactions, optionally filtered by type (null/ALL for all)
    List<FinancialTransactionDTO> getRecentTransactions(Long userId, String type, int limit);
}

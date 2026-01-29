package com.pm.farm_backend.Dto.farmerDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalTasks;
    private long completedTasks;
    private long activeCropCycles;
    private long pendingOrders;
    private long eligibleSchemes;
    private java.math.BigDecimal totalIncome;
    private java.math.BigDecimal totalExpense;
    private java.math.BigDecimal netProfit;
}

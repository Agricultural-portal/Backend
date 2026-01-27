package com.pm.farm_backend.Dto.buyerDTO;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardStatsDTO {
    private long totalOrders;
    private long pendingOrders;
    private long inTransitOrders;
    private long deliveredOrders;
    private BigDecimal totalSpent;
    private int cartItemsCount;
    private int favoritesCount;
}

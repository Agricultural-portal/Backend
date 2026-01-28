package com.pm.farm_backend.Dto.buyerDTO;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardStatsDTO {
    private long totalOrders;
    private long pendingOrders;
    private long cancelledOrders;
    private long deliveredOrders;
    private BigDecimal totalSpent;
    private int cartItemsCount;
    private int favoritesCount;
    
    // Additional getters/setters for compatibility
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
    public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
    public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
}

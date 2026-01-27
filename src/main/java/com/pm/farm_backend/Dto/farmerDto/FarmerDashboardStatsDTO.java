package com.pm.farm_backend.Dto.farmerDto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FarmerDashboardStatsDTO {
    
    private Long totalProducts;
    private Long activeProducts;
    private Long outOfStockProducts;
    
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal weeklyRevenue;
    
    private Double averageRating;
    private Long totalReviews;
    
    // Recent activity
    private Long newOrdersToday;
    private Long newOrdersThisWeek;
    
    // Top selling product
    private String topSellingProductName;
    private Long topSellingProductSales;
}
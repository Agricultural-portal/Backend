package com.pm.farm_backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalFarmers;
    private long totalBuyers;
    private long totalProducts;
    private BigDecimal platformRevenue;
    private List<UserSummary> recentBuyers;
    private List<UserSummary> recentFarmers;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String city;
        private String profileImageUrl;
        private String createdAt;
    }
}

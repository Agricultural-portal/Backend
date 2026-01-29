package com.pm.farm_backend.Service.DashboardService;

import com.pm.farm_backend.Dto.DashboardStatsResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardStatsResponse getDashboardStats() {
        // Placeholder implementation to satisfy dependency injection
        return new DashboardStatsResponse(
                0L, // totalFarmers
                0L, // totalBuyers
                0L, // totalProducts
                BigDecimal.ZERO, // platformRevenue
                Collections.emptyList(), // recentBuyers
                Collections.emptyList() // recentFarmers
        );
    }
}

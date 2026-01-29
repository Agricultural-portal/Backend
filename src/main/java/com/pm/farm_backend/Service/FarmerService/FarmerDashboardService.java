package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.DashboardStatsDTO;
import java.util.List;

public interface FarmerDashboardService {
    DashboardStatsDTO getStats(String email);

    List<com.pm.farm_backend.Dto.farmerDto.RecentTaskDTO> getRecentTasks(String email);
}

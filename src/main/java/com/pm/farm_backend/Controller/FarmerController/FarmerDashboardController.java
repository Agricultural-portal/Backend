package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.DashboardStatsDTO;

import com.pm.farm_backend.Service.FarmerService.FarmerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')")
public class FarmerDashboardController {

    private final FarmerDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats(java.security.Principal principal) {
        return ResponseEntity.ok(dashboardService.getStats(principal.getName()));
    }

    @GetMapping("/recent-tasks")
    public ResponseEntity<List<com.pm.farm_backend.Dto.farmerDto.RecentTaskDTO>> getRecentTasks(
            java.security.Principal principal) {
        return ResponseEntity.ok(dashboardService.getRecentTasks(principal.getName()));
    }
}

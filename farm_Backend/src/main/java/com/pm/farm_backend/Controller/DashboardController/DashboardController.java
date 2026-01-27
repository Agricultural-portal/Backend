package com.pm.farm_backend.Controller.DashboardController;

import com.pm.farm_backend.Dto.DashboardStatsResponse;
import com.pm.farm_backend.Service.DashboardService.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buyer/dashboard")
@Tag(name = "Dashboard", description = "Buyer Dashboard Statistics APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Get buyer dashboard statistics", description = "Get all buyer dashboard statistics including counts and recent orders")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}

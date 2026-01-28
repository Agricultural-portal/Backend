package com.pm.farm_backend.Controller.BuyerController;

import com.pm.farm_backend.Dto.buyerDTO.DashboardStatsDTO;
import com.pm.farm_backend.Service.buyerService.BuyerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/dashboard")
@PreAuthorize("hasRole('BUYER')")
public class BuyerDashboardController {

    @Autowired
    private BuyerDashboardService dashboardService;

    @GetMapping
    public DashboardStatsDTO getDashboardStats(Authentication authentication) {
        return dashboardService.getStats(authentication.getName());
    }
}

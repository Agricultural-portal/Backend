package com.pm.farm_backend.Controller.SystemSettingsController;

import com.pm.farm_backend.Dto.SystemSettingsRequest;
import com.pm.farm_backend.Dto.SystemSettingsResponse;
import com.pm.farm_backend.Service.SystemSettingsService.SystemSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@Tag(name = "System Settings", description = "System Configuration APIs")
@SecurityRequirement(name = "bearerAuth")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService settingsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system settings", description = "Retrieve all system configuration settings")
    public ResponseEntity<SystemSettingsResponse> getSettings() {
        SystemSettingsResponse settings = settingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update system settings", description = "Update system configuration settings")
    public ResponseEntity<SystemSettingsResponse> updateSettings(
            @RequestBody SystemSettingsRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        SystemSettingsResponse settings = settingsService.updateSettings(request, email);
        return ResponseEntity.ok(settings);
    }
}

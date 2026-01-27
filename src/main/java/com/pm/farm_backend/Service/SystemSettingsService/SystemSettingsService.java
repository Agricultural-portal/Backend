package com.pm.farm_backend.Service.SystemSettingsService;

import com.pm.farm_backend.Dto.SystemSettingsRequest;
import com.pm.farm_backend.Dto.SystemSettingsResponse;

public interface SystemSettingsService {
    SystemSettingsResponse getSettings();
    SystemSettingsResponse updateSettings(SystemSettingsRequest request, String userEmail);
    void initializeDefaultSettings();
}

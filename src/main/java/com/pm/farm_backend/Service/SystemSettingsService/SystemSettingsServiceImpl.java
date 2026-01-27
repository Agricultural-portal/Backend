package com.pm.farm_backend.Service.SystemSettingsService;

import com.pm.farm_backend.Dto.SystemSettingsRequest;
import com.pm.farm_backend.Dto.SystemSettingsResponse;
import com.pm.farm_backend.Model.SystemSettings;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.SystemSettingsRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {

    @Autowired
    private SystemSettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        initializeDefaultSettings();
    }

    @Override
    @Transactional
    public void initializeDefaultSettings() {
        if (settingsRepository.count() == 0) {
            SystemSettings settings = new SystemSettings();
            // Default values are set in the entity
            settings.setUpdatedBy(1L); // System default
            settingsRepository.save(settings);
        }
    }

    @Override
    public SystemSettingsResponse getSettings() {
        SystemSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("System settings not initialized"));

        return mapToResponse(settings);
    }

    @Override
    @Transactional
    public SystemSettingsResponse updateSettings(SystemSettingsRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SystemSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("System settings not initialized"));

        // Update all fields
        if (request.getPlatformCommissionRate() != null) {
            settings.setPlatformCommissionRate(request.getPlatformCommissionRate());
        }
        if (request.getMinimumOrderValue() != null) {
            settings.setMinimumOrderValue(request.getMinimumOrderValue());
        }
        if (request.getOrderAutoCancelHours() != null) {
            settings.setOrderAutoCancelHours(request.getOrderAutoCancelHours());
        }
        if (request.getRefundProcessingDays() != null) {
            settings.setRefundProcessingDays(request.getRefundProcessingDays());
        }
        if (request.getReturnWindowDays() != null) {
            settings.setReturnWindowDays(request.getReturnWindowDays());
        }
        if (request.getAutoApproveFarmers() != null) {
            settings.setAutoApproveFarmers(request.getAutoApproveFarmers());
        }
        if (request.getAutoApproveBuyers() != null) {
            settings.setAutoApproveBuyers(request.getAutoApproveBuyers());
        }
        if (request.getEmailVerificationRequired() != null) {
            settings.setEmailVerificationRequired(request.getEmailVerificationRequired());
        }
        if (request.getSessionTimeoutMinutes() != null) {
            settings.setSessionTimeoutMinutes(request.getSessionTimeoutMinutes());
        }
        if (request.getProductApprovalRequired() != null) {
            settings.setProductApprovalRequired(request.getProductApprovalRequired());
        }
        if (request.getMaxProductsPerFarmer() != null) {
            settings.setMaxProductsPerFarmer(request.getMaxProductsPerFarmer());
        }
        if (request.getMaxProductImageSizeMB() != null) {
            settings.setMaxProductImageSizeMB(request.getMaxProductImageSizeMB());
        }
        if (request.getCashOnDeliveryEnabled() != null) {
            settings.setCashOnDeliveryEnabled(request.getCashOnDeliveryEnabled());
        }
        if (request.getOnlinePaymentEnabled() != null) {
            settings.setOnlinePaymentEnabled(request.getOnlinePaymentEnabled());
        }
        if (request.getGstRate() != null) {
            settings.setGstRate(request.getGstRate());
        }

        settings.setUpdatedBy(user.getId());
        settingsRepository.save(settings);

        return mapToResponse(settings);
    }

    private SystemSettingsResponse mapToResponse(SystemSettings settings) {
        SystemSettingsResponse response = new SystemSettingsResponse();
        response.setId(settings.getId());
        response.setPlatformCommissionRate(settings.getPlatformCommissionRate());
        response.setMinimumOrderValue(settings.getMinimumOrderValue());
        response.setOrderAutoCancelHours(settings.getOrderAutoCancelHours());
        response.setRefundProcessingDays(settings.getRefundProcessingDays());
        response.setReturnWindowDays(settings.getReturnWindowDays());
        response.setAutoApproveFarmers(settings.getAutoApproveFarmers());
        response.setAutoApproveBuyers(settings.getAutoApproveBuyers());
        response.setEmailVerificationRequired(settings.getEmailVerificationRequired());
        response.setSessionTimeoutMinutes(settings.getSessionTimeoutMinutes());
        response.setProductApprovalRequired(settings.getProductApprovalRequired());
        response.setMaxProductsPerFarmer(settings.getMaxProductsPerFarmer());
        response.setMaxProductImageSizeMB(settings.getMaxProductImageSizeMB());
        response.setCashOnDeliveryEnabled(settings.getCashOnDeliveryEnabled());
        response.setOnlinePaymentEnabled(settings.getOnlinePaymentEnabled());
        response.setGstRate(settings.getGstRate());
        response.setUpdatedAt(settings.getUpdatedAt());

        // Get updatedBy user name
        if (settings.getUpdatedBy() != null) {
            userRepository.findById(settings.getUpdatedBy()).ifPresent(user -> {
                response.setUpdatedByName(user.getFirstName() + " " + user.getLastName());
            });
        }

        return response;
    }
}

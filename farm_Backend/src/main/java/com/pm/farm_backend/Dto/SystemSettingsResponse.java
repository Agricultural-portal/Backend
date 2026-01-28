package com.pm.farm_backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsResponse {
    private Long id;
    private BigDecimal platformCommissionRate;
    private BigDecimal minimumOrderValue;
    private Integer orderAutoCancelHours;
    private Integer refundProcessingDays;
    private Integer returnWindowDays;
    private Boolean autoApproveFarmers;
    private Boolean autoApproveBuyers;
    private Boolean emailVerificationRequired;
    private Integer sessionTimeoutMinutes;
    private Boolean productApprovalRequired;
    private Integer maxProductsPerFarmer;
    private Integer maxProductImageSizeMB;
    private Boolean cashOnDeliveryEnabled;
    private Boolean onlinePaymentEnabled;
    private BigDecimal gstRate;
    private LocalDateTime updatedAt;
    private String updatedByName;
}

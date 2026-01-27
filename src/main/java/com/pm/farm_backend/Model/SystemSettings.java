package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
@Data
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Revenue & Pricing
    @Column(nullable = false)
    private BigDecimal platformCommissionRate = new BigDecimal("5.00"); // Default 5%

    @Column(nullable = false)
    private BigDecimal minimumOrderValue = new BigDecimal("100.00");

    // Order Management
    @Column(nullable = false)
    private Integer orderAutoCancelHours = 24;

    @Column(nullable = false)
    private Integer refundProcessingDays = 7;

    @Column(nullable = false)
    private Integer returnWindowDays = 3;

    // User Management
    @Column(nullable = false)
    private Boolean autoApproveFarmers = true;

    @Column(nullable = false)
    private Boolean autoApproveBuyers = true;

    @Column(nullable = false)
    private Boolean emailVerificationRequired = false;

    @Column(nullable = false)
    private Integer sessionTimeoutMinutes = 60;

    // Product Management
    @Column(nullable = false)
    private Boolean productApprovalRequired = false;

    @Column(nullable = false)
    private Integer maxProductsPerFarmer = 100;

    @Column(nullable = false)
    private Integer maxProductImageSizeMB = 5;

    // Payment & Tax
    @Column(nullable = false)
    private Boolean cashOnDeliveryEnabled = true;

    @Column(nullable = false)
    private Boolean onlinePaymentEnabled = true;

    @Column(nullable = false)
    private BigDecimal gstRate = new BigDecimal("5.00");

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long updatedBy; // User ID who last updated
}

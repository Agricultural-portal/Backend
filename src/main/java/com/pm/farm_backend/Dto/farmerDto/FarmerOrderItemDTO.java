package com.pm.farm_backend.Dto.farmerDto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FarmerOrderItemDTO {
    private Long id;
    private Long orderId;
    private String productName;
    private String productUnit;
    private String productImageUrl;
    private String buyerName;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private String paymentStatus;
}

package com.pm.farm_backend.Dto.buyerDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlaceOrderRequest {

    private String shippingAddress;
    private BigDecimal totalAmount; // Total amount including taxes and fees
}

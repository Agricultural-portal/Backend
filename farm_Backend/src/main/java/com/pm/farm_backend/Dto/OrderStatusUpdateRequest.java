package com.pm.farm_backend.Dto;

import com.pm.farm_backend.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    
    @NotNull(message = "Status is required")
    private OrderStatus status;
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
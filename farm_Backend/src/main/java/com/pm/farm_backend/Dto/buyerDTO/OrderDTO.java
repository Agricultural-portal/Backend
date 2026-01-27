package com.pm.farm_backend.Dto.buyerDTO;

import com.pm.farm_backend.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}

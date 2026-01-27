package com.pm.farm_backend.Dto.buyerDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private BigDecimal totalAmount;
    private List<CartItemDTO> items;

    @Data
    public static class CartItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subTotal;
    }
}

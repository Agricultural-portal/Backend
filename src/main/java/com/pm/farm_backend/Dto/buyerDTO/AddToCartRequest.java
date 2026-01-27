package com.pm.farm_backend.Dto.buyerDTO;
import lombok.Data;

@Data
public class AddToCartRequest {
    private Long productId;
    private Integer quantity;
}
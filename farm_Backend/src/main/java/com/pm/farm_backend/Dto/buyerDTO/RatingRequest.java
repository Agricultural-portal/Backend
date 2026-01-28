package com.pm.farm_backend.Dto.buyerDTO;

import lombok.Data;

@Data
public class RatingRequest {

    private Long orderId;
    private Long productId;
    private int stars;
    private String comment;
}
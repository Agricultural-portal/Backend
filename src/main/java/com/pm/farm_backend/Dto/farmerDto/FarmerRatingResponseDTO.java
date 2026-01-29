package com.pm.farm_backend.Dto.farmerDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FarmerRatingResponseDTO {
    private Long id;
    private String buyerName;
    private String productName;
    private Integer stars;
    private String comment;
    private LocalDateTime createdAt;
}

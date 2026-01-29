package com.pm.farm_backend.Dto.farmerDto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String unit;
    private String category;
    private String imageUrl;
    private Long farmerId;
    private String status;
}

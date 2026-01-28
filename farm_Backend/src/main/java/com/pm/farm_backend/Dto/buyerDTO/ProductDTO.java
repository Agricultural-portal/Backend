package com.pm.farm_backend.Dto.buyerDTO;


import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.enums.ProductUnit;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductUnit unit;
    private ProductCategory category;
    private String imageUrl;
    private String farmerName;
}

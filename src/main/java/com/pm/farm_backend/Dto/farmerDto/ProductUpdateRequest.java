package com.pm.farm_backend.Dto.farmerDto;

import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.enums.ProductUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private ProductUnit unit;

    private ProductCategory category;

    private String imageUrl;

    private com.pm.farm_backend.enums.ProductStatus status;
}
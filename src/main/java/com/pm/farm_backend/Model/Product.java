package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.Model.User; // make sure User is imported

@Data
@Entity
@Table(name = "products")

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.pm.farm_backend.enums.ProductUnit unit; // e.g., KG, LITRE, DOZEN

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private com.pm.farm_backend.enums.ProductStatus status;

    private String imageUrl;

    @Column
    private Double averageRating = 0.0;

    @Column
    private Integer totalRatings = 0;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void reduceStock(int quantity) {
        if (this.stock < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + this.name);
        }
        this.stock -= quantity;
    }

    // Additional getters for compatibility
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public com.pm.farm_backend.enums.ProductUnit getUnit() {
        return unit;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public User getFarmer() {
        return farmer;
    }

    public com.pm.farm_backend.enums.ProductStatus getStatus() {
        return status;
    }
}

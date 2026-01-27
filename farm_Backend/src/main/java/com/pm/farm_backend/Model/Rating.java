package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ratings")
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long buyerId;
    private Long orderId;
    private Long productId;
    private Long farmerId;
    private int stars;
    private String comment;
}

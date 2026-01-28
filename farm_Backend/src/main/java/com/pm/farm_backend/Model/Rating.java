package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    
    @Column(nullable = false)
    private int rating; // Changed from 'stars' to 'rating' for consistency
    
    private String comment;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Getter/Setter for backward compatibility
    public int getStars() {
        return rating;
    }
    
    public void setStars(int stars) {
        this.rating = stars;
    }
}

package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "product_id"})
        }
)
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Additional getters/setters for compatibility
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}

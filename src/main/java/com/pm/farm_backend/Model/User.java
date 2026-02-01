package com.pm.farm_backend.Model;

import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String first_name;

    private String Last_name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String addresss;

    private String city;

    private String state;

    private String pincode;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private String profileImageUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal money = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    private Integer totalRatings = 0;
}

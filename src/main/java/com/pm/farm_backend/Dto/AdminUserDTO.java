package com.pm.farm_backend.Dto;

import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private AccountStatus status;
    private Role role;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private Double averageRating;
    private Integer totalRatings;
    
    // Address fields
    private String addresss;
    private String city;
    private String state;
    private String pincode;
    
    // Farmer-specific fields
    private String farmSize;
    private String farmType;
    
    // Financial
    private BigDecimal money;
}

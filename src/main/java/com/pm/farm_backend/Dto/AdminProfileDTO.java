package com.pm.farm_backend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AdminProfileDTO {
    private Long id;
    
    @JsonProperty("first_name")
    private String first_name;
    
    @JsonProperty("Last_name")
    private String Last_name;
    
    private String email;
    private String phone;
    private String addresss;
    private String city;
    private String state;
    private String pincode;
    private String profileImageUrl;
    private BigDecimal money;
    private BigDecimal averageRating;
    private Integer totalRatings;
    private LocalDateTime createdAt;
}

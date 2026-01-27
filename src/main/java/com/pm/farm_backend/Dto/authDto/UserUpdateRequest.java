package com.pm.farm_backend.Dto.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phone;
    
    private String addresss;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    // Farmer-specific fields (optional)
    private String farmSize;
    private String farmType;
}

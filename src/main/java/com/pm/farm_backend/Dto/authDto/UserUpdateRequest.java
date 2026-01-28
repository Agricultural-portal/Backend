package com.pm.farm_backend.Dto.authDto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    private String firstName;
    
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

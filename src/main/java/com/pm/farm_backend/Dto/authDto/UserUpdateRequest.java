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
    
    // Explicit getters for JSON properties
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddresss() { return addresss; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
    public String getFarmSize() { return farmSize; }
    public String getFarmType() { return farmType; }
}

package com.pm.farm_backend.Dto.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pm.farm_backend.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminSignupRequest {

    @NotBlank(message = "First Name is required")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String addresss;

    private String city;

    private String state;

    private String pincode;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

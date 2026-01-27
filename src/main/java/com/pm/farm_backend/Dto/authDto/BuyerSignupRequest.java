package com.pm.farm_backend.Dto.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pm.farm_backend.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BuyerSignupRequest {

    @NotBlank(message = "First Name is required")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @JsonProperty("last_name")
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String addresss;

    private String city;

    private String state;

    private String pincode;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

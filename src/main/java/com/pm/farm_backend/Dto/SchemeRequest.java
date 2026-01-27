package com.pm.farm_backend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchemeRequest {
    
    @NotBlank(message = "Scheme name is required")
    private String schemeName;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Benefits are required")
    private String benefits;
    
    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;
    
    private String applicationLink;
    
    public String getSchemeName() { return schemeName; }
    public String getDescription() { return description; }
    public String getBenefits() { return benefits; }
    public LocalDateTime getDeadline() { return deadline; }
    public String getApplicationLink() { return applicationLink; }
}
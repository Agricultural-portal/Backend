package com.pm.farm_backend.DTO;

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
    
    private String benefits;
    
    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;
    
    @NotBlank(message = "Application link is required")
    private String applicationLink;
}

package com.pm.farm_backend.Dto.farmerDto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class RecentTaskDTO {
    private Long id;
    private String title;
    private String status;
    private LocalDate dueDate;
    private Double expense;
    private String type; // "General" or "Crop Cycle"
}

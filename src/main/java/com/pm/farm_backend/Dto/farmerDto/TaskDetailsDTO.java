package com.pm.farm_backend.Dto.farmerDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TaskDetailsDTO {
    // private Long taskId;
    private String name;
    // private String description;
    // private String startDate;
    private LocalDate dueDate;
    private Double estimatedCost;
    private String category;
    private String priority;
    private String status;
}

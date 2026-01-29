package com.pm.farm_backend.Dto.farmerDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Double expense;
    private String category;
    private String priority;
    private String status;
    private Long cropCycleId;
    private String cropCycleName;
}

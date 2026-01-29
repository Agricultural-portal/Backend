package com.pm.farm_backend.Dto.farmerDto;

import com.pm.farm_backend.enums.Category;
import com.pm.farm_backend.enums.Priority;
import com.pm.farm_backend.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor

public class CreateTaskRequest {

        private String name;
        private String description;
        private TaskStatus status;
        // private Long farmerId;
        private LocalDate startDate;
        private LocalDate dueDate;
        private Double expense;
        private Category category;
        private Priority priority;
        private String cropCycleName;
        private Long cropCycleId;

}

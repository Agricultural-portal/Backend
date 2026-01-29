package com.pm.farm_backend.Model;

import com.pm.farm_backend.enums.Category;
import com.pm.farm_backend.enums.Priority;
import com.pm.farm_backend.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    // private String category;

    @ManyToOne
    @JoinColumn(name = "crop_cycle_id", nullable = true)
    private CropCycle cropCycle;

    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;
    private LocalDate startDate;
    private LocalDate dueDate;

    private Double expense;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}

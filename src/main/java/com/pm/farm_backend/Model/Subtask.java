package com.pm.farm_backend.Model;

import com.pm.farm_backend.enums.TaskPriority;
import com.pm.farm_backend.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "subtask")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status != 'DELETED'")
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskTitle;

    @Column(length = 500)
    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    private Double expense;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_cycle_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private CropCycle cropCycle;
}

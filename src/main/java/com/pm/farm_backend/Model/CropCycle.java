package com.pm.farm_backend.Model;

import com.pm.farm_backend.enums.CropStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "crop_cycle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status != 'DELETED'")
public class CropCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cropName;

    private String variety;

    @Column(nullable = false)
    private LocalDate sowingDate;

    private LocalDate expectedHarvestDate;

    @Column(nullable = false)
    private Double totalArea;

    private Double totalExpense;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private FarmerProfile farmer;

    @OneToMany(mappedBy = "cropCycle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Where(clause = "status != 'DELETED'")
    private List<Subtask> subtasks = new ArrayList<>();
}

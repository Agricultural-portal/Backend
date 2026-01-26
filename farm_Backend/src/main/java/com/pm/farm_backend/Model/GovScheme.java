package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "gov_schemes")
public class GovScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schemeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String benefits;

    private LocalDateTime deadline;

    private String applicationLink;

    private boolean isActive;
    
    }

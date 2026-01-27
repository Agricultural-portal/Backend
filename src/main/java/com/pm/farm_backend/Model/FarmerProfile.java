package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String farmSize;
    private String farmType;

}

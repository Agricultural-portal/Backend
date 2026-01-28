package com.pm.farm_backend.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BankDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountHolderName;

    private String accountNumber;

    private String ifscCode;

    private String bankName;

    private String branch;

    private String upiId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

}
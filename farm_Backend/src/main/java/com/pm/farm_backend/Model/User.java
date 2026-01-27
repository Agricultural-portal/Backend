package com.pm.farm_backend.Model;

import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String addresss;

    private String city;

    private String state;

    private String pincode;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private String profileImageUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal money = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public void setFullName(String fullName) {
        if (fullName != null && fullName.contains(" ")) {
            String[] parts = fullName.split(" ", 2);
            this.firstName = parts[0];
            this.lastName = parts[1];
        } else {
            this.firstName = fullName;
            this.lastName = "";
        }
    }

    public String getLocation() {
        return this.addresss;
    }

    public void setLocation(String location) {
        this.addresss = location;
    }
    
    // Additional getters/setters for compatibility
    public Long getId() { return id; }
    public Role getRole() { return role; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}

package com.pm.farm_backend.Controller.AuthController;

import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(AccountStatus.ACTIVE);
        userRepo.save(user);

        return ResponseEntity.ok("User approved");
    }

    @GetMapping("/pending")
    public ResponseEntity<java.util.List<User>> getPendingAdmins() {
        return ResponseEntity
                .ok(userRepo.findByRoleAndStatus(com.pm.farm_backend.enums.Role.ADMIN, AccountStatus.PENDING));
    }

    @GetMapping("/farmers")
    public ResponseEntity<java.util.List<User>> getAllFarmers() {
        return ResponseEntity.ok(userRepo.findByRole(com.pm.farm_backend.enums.Role.FARMER));
    }

    @GetMapping("/buyers")
    public ResponseEntity<java.util.List<User>> getAllBuyers() {
        return ResponseEntity.ok(userRepo.findByRole(com.pm.farm_backend.enums.Role.BUYER));
    }

    @GetMapping("/count/total-farmers")
    public ResponseEntity<Long> getTotalFarmersCount() {
        return ResponseEntity.ok(userRepo.countByRole(Role.FARMER));
    }

    @GetMapping("/count/active-farmers")
    public ResponseEntity<Long> getActiveFarmersCount() {
        return ResponseEntity.ok(userRepo.countByRoleAndStatus(Role.FARMER, AccountStatus.ACTIVE));
    }

    @GetMapping("/count/inactive-farmers")
    public ResponseEntity<Long> getInactiveFarmersCount() {
        return ResponseEntity.ok(userRepo.countByRoleAndStatus(Role.FARMER, AccountStatus.SUSPENDED));
    }

    @GetMapping("/count/new-farmers-this-month")
    public ResponseEntity<Long> getNewFarmersThisMonthCount() {
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0)
                .withMinute(0).withSecond(0).withNano(0);
        return ResponseEntity.ok(userRepo.countByRoleAndCreatedAtAfter(Role.FARMER, startOfMonth));
    }

    @GetMapping("/count/total-buyers")
    public ResponseEntity<Long> getTotalBuyersCount() {
        return ResponseEntity.ok(userRepo.countByRole(Role.BUYER));
    }

    @GetMapping("/count/active-buyers")
    public ResponseEntity<Long> getActiveBuyersCount() {
        return ResponseEntity.ok(userRepo.countByRoleAndStatus(Role.BUYER, AccountStatus.ACTIVE));
    }
}

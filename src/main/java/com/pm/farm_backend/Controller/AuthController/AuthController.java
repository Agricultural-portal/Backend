package com.pm.farm_backend.Controller.AuthController;

import com.pm.farm_backend.Dto.LoginRequest;
import com.pm.farm_backend.Dto.authDto.BuyerSignupRequest;
import com.pm.farm_backend.Dto.authDto.FarmerSignupRequest;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Service.AuthService.AuthService;
import com.pm.farm_backend.Service.FileLoggerService;
import com.pm.farm_backend.Security.model.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FileLoggerService fileLoggerService;

    @PostMapping("/signup/farmer")
    public ResponseEntity<?> farmerSignup(@RequestBody @Valid FarmerSignupRequest dto) {
        authService.registerFarmer(dto);
        return ResponseEntity.ok("Farmer registered. Awaiting approval.");
    }

    @PostMapping("/signup/buyer")
    public ResponseEntity<?> buyerSignup(@RequestBody @Valid BuyerSignupRequest dto) {
        authService.registerBuyer(dto);
        return ResponseEntity.ok("Buyer registered successfully.");
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<?> adminSignup(@RequestBody @Valid com.pm.farm_backend.Dto.authDto.AdminSignupRequest dto) {
        authService.registerAdmin(dto);
        return ResponseEntity.ok("Admin registered. Awaiting approval.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);

        // Get user details for response
        User user = authService.getUserByEmail(request.getEmail());

        Map<String, Object> response = Map.of(
                "token", token,
                "id", user.getId(),
                "firstName", user.getFirst_name(),
                "lastName", user.getLast_name(),
                "email", user.getEmail(),
                "role", user.getRole().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            fileLoggerService.logLogout(userDetails.getUsername());
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}

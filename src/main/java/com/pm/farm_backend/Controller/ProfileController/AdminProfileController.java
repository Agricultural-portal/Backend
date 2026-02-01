package com.pm.farm_backend.Controller.ProfileController;

import com.pm.farm_backend.Dto.AdminProfileDTO;
import com.pm.farm_backend.Dto.authDto.UserUpdateRequest;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Service.ImageService;
import com.pm.farm_backend.Service.ProfileService.AdminProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/profile")
@SecurityRequirement(name = "bearerAuth")
public class AdminProfileController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private AdminProfileService adminProfileService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminProfileDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        AdminProfileDTO profile = adminProfileService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProfile(
            @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            AdminProfileDTO updatedProfile = adminProfileService.updateProfile(email, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProfileImage(
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            String imageUrl = imageService.uploadImage(image);
            adminProfileService.updateProfileImage(email, imageUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile image updated successfully");
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

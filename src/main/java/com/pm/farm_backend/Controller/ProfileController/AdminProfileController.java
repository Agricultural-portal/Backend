package com.pm.farm_backend.Controller.ProfileController;

import com.pm.farm_backend.Service.ImageService;
import com.pm.farm_backend.Service.ProfileService.AdminProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/profile")
public class AdminProfileController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private AdminProfileService adminProfileService;

    @PostMapping("/image")
    public ResponseEntity<?> updateProfileImage(
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            String imageUrl = imageService.uploadImage(image);
            adminProfileService.updateProfileImage(email, imageUrl);
            return ResponseEntity.ok("Profile image updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

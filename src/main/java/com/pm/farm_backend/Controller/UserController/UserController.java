package com.pm.farm_backend.Controller.UserController;

import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.enums.Role;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.Service.ImageService;
import com.pm.farm_backend.Dto.authDto.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @GetMapping("/{id}")
    public User getProfile(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("fullName", user.getFirstName() + " " + user.getLastName());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("city", user.getCity());
        response.put("state", user.getState());
        response.put("address", user.getAddresss());
        response.put("pincode", user.getPincode());
        response.put("role", user.getRole().name());
        response.put("profileImageUrl", user.getProfileImageUrl());
        response.put("status", user.getStatus().name());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public User updateProfile(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdates) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        if(userUpdates.getFirstName() != null) user.setFirstName(userUpdates.getFirstName());
        if(userUpdates.getLastName() != null) user.setLastName(userUpdates.getLastName());
        if(userUpdates.getPhone() != null) user.setPhone(userUpdates.getPhone());
        if(userUpdates.getCity() != null) user.setCity(userUpdates.getCity());
        if(userUpdates.getState() != null) user.setState(userUpdates.getState());
        if(userUpdates.getAddresss() != null) user.setAddresss(userUpdates.getAddresss());
        if(userUpdates.getPincode() != null) user.setPincode(userUpdates.getPincode());
        
        return userRepository.save(user);
    }

    @PostMapping("/{id}/profile-image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            
            String imageUrl = imageService.uploadImage(file);
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "Profile image updated successfully",
                "imageUrl", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfileImageUrl(null);
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of("message", "Profile image removed successfully"));
    }

    @GetMapping("/farmers")
    public List<User> getAllFarmers() {
        return userRepository.findByRole(Role.FARMER);
    }

    @GetMapping("/buyers")
    public List<User> getAllBuyers() {
        return userRepository.findByRole(Role.BUYER);
    }
}

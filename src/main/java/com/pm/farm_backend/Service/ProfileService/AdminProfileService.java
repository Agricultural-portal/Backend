package com.pm.farm_backend.Service.ProfileService;

import com.pm.farm_backend.Dto.AdminProfileDTO;
import com.pm.farm_backend.Dto.authDto.UserUpdateRequest;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminProfileService {

    @Autowired
    private UserRepository userRepository;

    public AdminProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Transactional
    public AdminProfileDTO updateProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirst_name(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLast_name(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddresss() != null) {
            user.setAddresss(request.getAddresss());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getState() != null) {
            user.setState(request.getState());
        }
        if (request.getPincode() != null) {
            user.setPincode(request.getPincode());
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public void updateProfileImage(String email, String imageUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    private AdminProfileDTO convertToDTO(User user) {
        AdminProfileDTO.AdminProfileDTOBuilder builder = AdminProfileDTO.builder()
            .id(user.getId())
            .first_name(user.getFirst_name())
            .Last_name(user.getLast_name())
            .email(user.getEmail())
            .phone(user.getPhone())
            .addresss(user.getAddresss())
            .city(user.getCity())
            .state(user.getState())
            .pincode(user.getPincode())
            .profileImageUrl(user.getProfileImageUrl())
            .money(user.getMoney())
            .createdAt(user.getCreatedAt());
        
        if (user.getAverageRating() != null) {
            builder.averageRating(user.getAverageRating());
        }
        if (user.getTotalRatings() != null) {
            builder.totalRatings(user.getTotalRatings());
        }
        
        return builder.build();
    }
}

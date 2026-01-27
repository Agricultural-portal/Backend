package com.pm.farm_backend.Service.ProfileService;

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

    public User getProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateProfile(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
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

        return userRepository.save(user);
    }

    @Transactional
    public void updateProfileImage(String email, String imageUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }
}

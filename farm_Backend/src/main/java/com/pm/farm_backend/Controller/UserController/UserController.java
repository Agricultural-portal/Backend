package com.pm.farm_backend.Controller.UserController;

import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.enums.Role;
import com.pm.farm_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public User getProfile(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/{id}")
    public User updateProfile(@PathVariable Long id, @RequestBody User userUpdates) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        if(userUpdates.getFullName() != null) user.setFullName(userUpdates.getFullName());
        if(userUpdates.getPhone() != null) user.setPhone(userUpdates.getPhone());
        if(userUpdates.getLocation() != null) user.setLocation(userUpdates.getLocation());
        // Password update should be handled securely, skipping for simple update
        
        return userRepository.save(user);
    }

    @GetMapping("/farmers")
    public List<User> getAllFarmers() {
        return userRepository.findByRole(Role.FARMER);
    }
}

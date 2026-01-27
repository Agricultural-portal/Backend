package com.pm.farm_backend.Service.AuthService;

import com.pm.farm_backend.Dto.LoginRequest;
import com.pm.farm_backend.Dto.authDto.BuyerSignupRequest;
import com.pm.farm_backend.Dto.authDto.FarmerSignupRequest;
import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.Security.Util.JwtService;
import com.pm.farm_backend.Security.model.CustomUserDetails;
import com.pm.farm_backend.Service.SystemSettingsService.SystemSettingsService;
import com.pm.farm_backend.Dto.SystemSettingsResponse;
import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerProfileRepository farmerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SystemSettingsService systemSettingsService;

    public void registerFarmer(FarmerSignupRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setFirst_name(dto.getFirst_name());

        user.setLast_name(dto.getLast_name());

        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.FARMER);
        
        // Check autoApproveFarmers setting
        SystemSettingsResponse settings = systemSettingsService.getSettings();
        user.setStatus(settings.getAutoApproveFarmers() ? AccountStatus.ACTIVE : AccountStatus.PENDING);
        
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setAddresss(dto.getAddresss());
        user.setPincode(dto.getPincode());

        User savedUser = userRepository.save(user);

        FarmerProfile profile = new FarmerProfile();
        profile.setUser(savedUser);
        profile.setFarmSize(dto.getFarmSize());
        profile.setFarmType(dto.getFarmType());

        farmerRepo.save(profile);
    }

    public void registerBuyer(BuyerSignupRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirst_name(dto.getFirst_name());
        user.setLast_name(dto.getLast_name());

        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddresss(dto.getAddresss()); // Mapping location to addresss
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setPincode(dto.getPincode());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.BUYER);
        
        // Check autoApproveBuyers setting
        SystemSettingsResponse settings = systemSettingsService.getSettings();
        user.setStatus(settings.getAutoApproveBuyers() ? AccountStatus.ACTIVE : AccountStatus.PENDING);

        userRepository.save(user);
    }

    public void registerAdmin(com.pm.farm_backend.Dto.authDto.AdminSignupRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirst_name(dto.getFirst_name());
        user.setLast_name(dto.getLast_name());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddresss(dto.getAddresss());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setPincode(dto.getPincode());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ADMIN);
        user.setStatus(AccountStatus.ACTIVE); // REQUIRES APPROVAL

        userRepository.save(user);
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add user role to JWT claims
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());

        return jwtService.generateToken(claims, new CustomUserDetails(user));
    }
}

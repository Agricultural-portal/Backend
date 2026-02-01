package com.pm.farm_backend.Service.AuthService;

import com.pm.farm_backend.Dto.LoginRequest;
import com.pm.farm_backend.Dto.authDto.BuyerSignupRequest;
import com.pm.farm_backend.Dto.authDto.FarmerSignupRequest;
import com.pm.farm_backend.Exception.DuplicateResourceException;
import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.Security.Util.JwtService;
import com.pm.farm_backend.Security.model.CustomUserDetails;
import com.pm.farm_backend.Service.SystemSettingsService.SystemSettingsService;
import com.pm.farm_backend.Service.FileLoggerService;
import com.pm.farm_backend.Dto.SystemSettingsResponse;
import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Autowired
    private FileLoggerService fileLoggerService;

    public void registerFarmer(FarmerSignupRequest dto) {
        try {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                fileLoggerService.logRegistrationFailure(dto.getEmail(), "FARMER", "Email already exists");
                throw new DuplicateResourceException(
                        "This email is already registered. Please use a different email or try logging in.");
            }

            User user = new User();

            user.setFirst_name(dto.getFirstName());

            user.setLast_name(dto.getLastName());

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

            // Log successful registration
            fileLoggerService.logRegistrationSuccess(dto.getEmail(), "FARMER");
        } catch (DuplicateResourceException e) {
            throw e; // Re-throw to maintain existing behavior
        } catch (Exception e) {
            fileLoggerService.logRegistrationFailure(dto.getEmail(), "FARMER", e.getMessage());
            throw e;
        }
    }

    public void registerBuyer(BuyerSignupRequest dto) {
        try {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                fileLoggerService.logRegistrationFailure(dto.getEmail(), "BUYER", "Email already exists");
                throw new DuplicateResourceException(
                        "This email is already registered. Please use a different email or try logging in.");
            }

            User user = new User();
            user.setFirst_name(dto.getFirstName());
            user.setLast_name(dto.getLastName());

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

            // Log successful registration
            fileLoggerService.logRegistrationSuccess(dto.getEmail(), "BUYER");
        } catch (DuplicateResourceException e) {
            throw e; // Re-throw to maintain existing behavior
        } catch (Exception e) {
            fileLoggerService.logRegistrationFailure(dto.getEmail(), "BUYER", e.getMessage());
            throw e;
        }
    }

    public void registerAdmin(com.pm.farm_backend.Dto.authDto.AdminSignupRequest dto) {
        try {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                fileLoggerService.logRegistrationFailure(dto.getEmail(), "ADMIN", "Email already exists");
                throw new DuplicateResourceException(
                        "This email is already registered. Please use a different email or try logging in.");
            }

            User user = new User();
            user.setFirst_name(dto.getFirstName());
            user.setLast_name(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setAddresss(dto.getAddresss());
            user.setCity(dto.getCity());
            user.setState(dto.getState());
            user.setPincode(dto.getPincode());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setRole(Role.ADMIN);
            user.setStatus(AccountStatus.PENDING); // REQUIRES APPROVAL

            userRepository.save(user);

            // Log successful registration
            fileLoggerService.logRegistrationSuccess(dto.getEmail(), "ADMIN");
        } catch (DuplicateResourceException e) {
            throw e; // Re-throw to maintain existing behavior
        } catch (Exception e) {
            fileLoggerService.logRegistrationFailure(dto.getEmail(), "ADMIN", e.getMessage());
            throw e;
        }
    }

    public String login(LoginRequest request) {
        try {
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

            // Log successful login
            fileLoggerService.logLoginSuccess(request.getEmail());

            return jwtService.generateToken(claims, new CustomUserDetails(user));
        } catch (BadCredentialsException e) {
            fileLoggerService.logLoginFailure(request.getEmail(), "Invalid credentials");
            throw e;
        } catch (Exception e) {
            fileLoggerService.logLoginFailure(request.getEmail(), e.getMessage());
            throw e;
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

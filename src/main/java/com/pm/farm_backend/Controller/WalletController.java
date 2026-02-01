package com.pm.farm_backend.Controller;

import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@Slf4j
public class WalletController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getWalletBalance(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("balance", user.getMoney());
        response.put("userId", user.getId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMoney(
            @RequestBody Map<String, BigDecimal> request,
            Authentication authentication) {
        
        String email = authentication.getName();
        BigDecimal amount = request.get("amount");

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid amount"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal currentBalance = user.getMoney() != null ? user.getMoney() : BigDecimal.ZERO;
        user.setMoney(currentBalance.add(amount));
        userRepository.save(user);

        log.info("Money added to wallet - User: {}, Amount: {}, New Balance: {}", 
                email, amount, user.getMoney());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newBalance", user.getMoney());
        response.put("addedAmount", amount);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactionHistory(Authentication authentication) {
        // For now, return basic info. Can be extended with transaction history table
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("currentBalance", user.getMoney());
        response.put("userId", user.getId());
        
        return ResponseEntity.ok(response);
    }
}

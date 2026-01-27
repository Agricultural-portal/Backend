package com.pm.farm_backend.Controller.BankDetailsController;

import com.pm.farm_backend.Model.BankDetails;
import com.pm.farm_backend.Service.BankDetailsService.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/farmer/bankdetails")
public class BankDetailsController {

    @Autowired
    private BankDetailsService bankDetailsService;

    @PostMapping
    public ResponseEntity<BankDetails> createBankDetails(
            @RequestBody BankDetails bankDetails) {

        BankDetails saved = bankDetailsService.createBankDetails(bankDetails);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<BankDetails> updateBankDetails(
            @PathVariable Long userId,
            @RequestBody BankDetails bankDetails) {
        BankDetails updated = bankDetailsService.updateBankDetails(userId, bankDetails);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BankDetails> getBankDetailsByUser(@PathVariable Long userId) {
        BankDetails bankDetails = bankDetailsService.getBankDetailsByUserId(userId);
        return ResponseEntity.ok(bankDetails);
    }

}


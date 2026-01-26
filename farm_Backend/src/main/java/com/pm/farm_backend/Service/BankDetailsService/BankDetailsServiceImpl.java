package com.pm.farm_backend.Service.BankDetailsService;

import com.pm.farm_backend.Model.BankDetails;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.BankDetailsRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankDetailsServiceImpl implements BankDetailsService {

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public BankDetails createBankDetails(BankDetails bankDetails) {
        Long userId = bankDetails.getUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if bank details already exist
        return bankDetailsRepository.findByUser(user)
                .map(existing -> {
                    // Update existing
                    updateFields(existing, bankDetails);
                    return bankDetailsRepository.save(existing);
                })
                .orElseGet(() -> {
                    // Create new
                    bankDetails.setUser(user);
                    return bankDetailsRepository.save(bankDetails);
                });
    }

    @Override
    public BankDetails updateBankDetails(Long userId, BankDetails bankDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankDetails existing = bankDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Bank details not found. Please create first."));

        updateFields(existing, bankDetails);
        return bankDetailsRepository.save(existing);
    }

    @Override
    public BankDetails getBankDetailsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bankDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Bank details not found for user"));
    }

    private void updateFields(BankDetails existing, BankDetails newDetails) {
        existing.setAccountHolderName(newDetails.getAccountHolderName());
        existing.setAccountNumber(newDetails.getAccountNumber());
        existing.setIfscCode(newDetails.getIfscCode());
        existing.setBankName(newDetails.getBankName());
        existing.setBranch(newDetails.getBranch());
        existing.setUpiId(newDetails.getUpiId());
    }
}


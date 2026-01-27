package com.pm.farm_backend.Service.BankDetailsService;

import com.pm.farm_backend.Model.BankDetails;

public interface BankDetailsService {
    BankDetails createBankDetails(BankDetails bankDetails);

    BankDetails updateBankDetails(Long userId, BankDetails bankDetails);

    BankDetails getBankDetailsByUserId(Long userId);
}
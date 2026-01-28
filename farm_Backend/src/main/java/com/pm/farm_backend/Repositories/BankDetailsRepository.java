package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.BankDetails;
import com.pm.farm_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    Optional<BankDetails> findByUser(User user);
}
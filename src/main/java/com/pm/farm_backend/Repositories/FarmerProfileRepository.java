package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
    Optional<FarmerProfile> findByUser(User user);
}

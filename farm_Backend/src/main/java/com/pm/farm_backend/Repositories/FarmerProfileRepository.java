package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
}

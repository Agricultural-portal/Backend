package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    Optional<SystemSettings> findFirstByOrderByIdAsc();
}

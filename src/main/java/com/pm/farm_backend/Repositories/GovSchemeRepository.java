package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.GovScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GovSchemeRepository extends JpaRepository<GovScheme, Long> {
    List<GovScheme> findByIsActive(boolean isActive);
    
    long countByIsActive(boolean isActive);
    
    List<GovScheme> findBySchemeNameContainingIgnoreCase(String schemeName);
}

package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.GovScheme;
import java.util.List;
import java.util.Optional;

public interface GovSchemeService {
    List<GovScheme> getAllSchemes();

    Optional<GovScheme> getSchemeById(Long id);
}

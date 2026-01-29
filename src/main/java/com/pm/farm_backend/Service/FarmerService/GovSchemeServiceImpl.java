package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.GovScheme;
import com.pm.farm_backend.Repositories.GovSchemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GovSchemeServiceImpl implements GovSchemeService {

    @Autowired
    private GovSchemeRepository govSchemeRepository;

    @Override
    public List<GovScheme> getAllSchemes() {
        return govSchemeRepository.findAll();
    }

    @Override
    public Optional<GovScheme> getSchemeById(Long id) {
        return govSchemeRepository.findById(id);
    }
}

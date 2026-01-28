package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByFarmerId(Long farmerId);
    List<Rating> findByProductId(Long productId);
    List<Rating> findByBuyerId(Long buyerId);
}

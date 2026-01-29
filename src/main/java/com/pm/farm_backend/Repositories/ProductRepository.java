package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByFarmer(User farmer);

    List<Product> findByFarmerId(Long farmerId);

    List<Product> findByFarmer_Id(Long farmerId);

    List<Product> findByFarmer_IdAndStatus(Long farmerId, com.pm.farm_backend.enums.ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.farmer.id = :farmerId AND p.stock > 0")
    List<Product> findActiveProductsByFarmerId(@Param("farmerId") Long farmerId);
}

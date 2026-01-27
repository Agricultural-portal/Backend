package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByNameContainingIgnoreCase(String name);
}

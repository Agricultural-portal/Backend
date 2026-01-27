package com.pm.farm_backend.Controller.BuyerController;

import com.pm.farm_backend.Dto.buyerDTO.ProductDTO;
import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.Service.buyerService.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/products")
@PreAuthorize("hasRole('BUYER')")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    public List<ProductDTO> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

    @GetMapping("/category/{category}")
    public List<ProductDTO> getProductsByCategory(@PathVariable ProductCategory category) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}

package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.ProductRequestDTO;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Service.FarmerService.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')")
public class FarmerProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestPart("product") ProductRequestDTO product,
            @RequestPart(value = "image", required = false) MultipartFile image,
            java.security.Principal principal) {
        return ResponseEntity.ok(
                productService.addProduct(product, principal.getName(), image));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getFarmerProducts(java.security.Principal principal) {
        return ResponseEntity.ok(productService.getAllProducts(principal.getName()));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductRequestDTO product,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
            java.security.Principal principal) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, product, principal.getName(), image));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, java.security.Principal principal) {
        productService.deleteProduct(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

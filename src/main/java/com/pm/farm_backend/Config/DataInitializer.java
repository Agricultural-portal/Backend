package com.pm.farm_backend.Config;

import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.SystemSettings;
import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.SystemSettingsRepository;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.enums.Role;
import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.enums.ProductUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize system settings first
        if (systemSettingsRepository.count() == 0) {
            SystemSettings settings = new SystemSettings();
            settings.setUpdatedBy(1L); // Will be updated after creating admin
            systemSettingsRepository.save(settings);
        }

        // Create admin user if not exists
        if (userRepository.findByEmail("admin@demo.com").isEmpty()) {
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEmail("admin@demo.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(AccountStatus.ACTIVE);
            admin.setPhone("9999999999");
            admin.setAddresss("System");
            admin.setCity("System");
            admin.setState("System");
            admin.setPincode("000000");
            userRepository.save(admin);
        }

        // Create buyer user if not exists
        if (userRepository.findByEmail("buyer@demo.com").isEmpty()) {
            User buyer = new User();
            buyer.setFirstName("John");
            buyer.setLastName("Smith");
            buyer.setEmail("buyer@demo.com");
            buyer.setPasswordHash(passwordEncoder.encode("buyer123"));
            buyer.setRole(Role.BUYER);
            buyer.setStatus(AccountStatus.ACTIVE);
            buyer.setPhone("9876543210");
            buyer.setAddresss("123 Main Street");
            buyer.setCity("Mumbai");
            buyer.setState("Maharashtra");
            buyer.setPincode("400001");
            userRepository.save(buyer);
        }

        // Create farmer users if not exist
        createFarmerIfNotExists("farmer1@demo.com", "Rajesh", "Kumar", "Village Khanna", "Khanna", "Punjab", "141401",
                "5 acres", "Organic");
        createFarmerIfNotExists("farmer2@demo.com", "Priya", "Sharma", "Village Ludhiana", "Ludhiana", "Punjab",
                "141002", "3 acres", "Traditional");
        createFarmerIfNotExists("farmer3@demo.com", "Amit", "Singh", "Village Amritsar", "Amritsar", "Punjab", "143001",
                "7 acres", "Organic");

        // Create products if not exist
        // NOTE: Adding a few test products to verify backend integration
        // Products will be added by farmers later through farmer interface
        if (productRepository.count() == 0) {
            createTestProducts();
        }
    }

    private void createFarmerIfNotExists(String email, String firstName, String lastName,
            String address, String city, String state, String pincode,
            String farmSize, String farmType) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User farmer = new User();
            farmer.setFirstName(firstName);
            farmer.setLastName(lastName);
            farmer.setEmail(email);
            farmer.setPasswordHash(passwordEncoder.encode("farmer123"));
            farmer.setRole(Role.FARMER);
            farmer.setStatus(AccountStatus.ACTIVE);
            farmer.setPhone("9876543210");
            farmer.setAddresss(address);
            farmer.setCity(city);
            farmer.setState(state);
            farmer.setPincode(pincode);
            User savedFarmer = userRepository.save(farmer);

            // Create farmer profile
            FarmerProfile profile = new FarmerProfile();
            profile.setUser(savedFarmer);
            profile.setFarmSize(farmSize);
            profile.setFarmType(farmType);
            farmerProfileRepository.save(profile);
        }
    }

    private void createTestProducts() {
        User farmer1 = userRepository.findByEmail("farmer1@demo.com").orElse(null);
        User farmer2 = userRepository.findByEmail("farmer2@demo.com").orElse(null);
        User farmer3 = userRepository.findByEmail("farmer3@demo.com").orElse(null);

        if (farmer1 != null) {
            // Product with NULL imageUrl - will show default image until farmer uploads
            createProduct("Fresh Tomatoes", "Organic red tomatoes from farm",
                    new BigDecimal("80"), 100, ProductUnit.KG, ProductCategory.VEGETABLES,
                    null, farmer1);

            // Product with NULL imageUrl - will show default image until farmer uploads
            createProduct("Basmati Rice", "Premium quality basmati rice",
                    new BigDecimal("120"), 50, ProductUnit.KG, ProductCategory.GRAINS,
                    null, farmer1);
        }

        if (farmer2 != null) {
            // Product with NULL imageUrl - will show default image until farmer uploads
            createProduct("Fresh Spinach", "Organic green spinach leaves",
                    new BigDecimal("40"), 80, ProductUnit.KG, ProductCategory.VEGETABLES,
                    null, farmer2);

            // Product with NULL imageUrl - will show default image until farmer uploads
            createProduct("Wheat Flour", "Stone ground wheat flour",
                    new BigDecimal("60"), 200, ProductUnit.KG, ProductCategory.GRAINS,
                    null, farmer2);
        }

        if (farmer3 != null) {
            // Product with NULL imageUrl - will show default image until farmer uploads
            createProduct("Fresh Carrots", "Sweet orange carrots",
                    new BigDecimal("50"), 120, ProductUnit.KG, ProductCategory.VEGETABLES,
                    null, farmer3);

            // Product with NULL imageUrl - will show default image until farmer uploads
            createProduct("Organic Apples", "Fresh red apples from organic farm",
                    new BigDecimal("150"), 60, ProductUnit.KG, ProductCategory.FRUITS,
                    null, farmer3);
        }
    }

    private void createProduct(String name, String description, BigDecimal price,
            Integer stock, ProductUnit unit, ProductCategory category,
            String imageUrl, User farmer) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setUnit(unit);
        product.setCategory(category);
        product.setImageUrl(imageUrl); // This can be NULL or actual URL
        product.setStatus(com.pm.farm_backend.enums.ProductStatus.AVAILABLE);
        product.setFarmer(farmer);
        productRepository.save(product);
    }
}
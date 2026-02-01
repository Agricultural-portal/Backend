package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.Cart;
import com.pm.farm_backend.Model.CartItem;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Dto.buyerDTO.AddToCartRequest;
import com.pm.farm_backend.Dto.buyerDTO.CartDTO;
import com.pm.farm_backend.Service.NotificationService;
import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import com.pm.farm_backend.Repositories.CartRepository;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public CartDTO addToCart(AddToCartRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);

        // Create notification for adding item to cart
        try {
            CreateNotificationDto notificationDto = new CreateNotificationDto();
            notificationDto.setUserId(user.getId());
            notificationDto.setType(NotificationType.PRODUCT);
            notificationDto.setTitle("Item Added to Cart");
            notificationDto.setMessage(product.getName() + " (Quantity: " + request.getQuantity() + ") has been added to your cart");
            notificationDto.setPriority(NotificationPriority.LOW);
            notificationService.createNotification(notificationDto);
            log.info("Notification created for cart item: {}", product.getName());
        } catch (Exception e) {
            log.error("Failed to create notification for cart: {}", e.getMessage());
        }

        return mapToDTO(savedCart);
    }

    public CartDTO getCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return mapToDTO(cart);
    }
    
    @Transactional
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if(cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

    private CartDTO mapToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setTotalAmount(cart.getTotalAmount());
        dto.setItems(cart.getItems().stream().map(item -> {
            CartDTO.CartItemDTO itemDto = new CartDTO.CartItemDTO();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setPrice(item.getProduct().getPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setSubTotal(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
            return itemDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}

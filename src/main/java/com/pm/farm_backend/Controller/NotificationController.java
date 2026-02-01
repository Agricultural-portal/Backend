package com.pm.farm_backend.Controller;

import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Dto.NotificationDto;
import com.pm.farm_backend.Dto.NotificationPreferenceDto;
import com.pm.farm_backend.Dto.NotificationResponseDto;
import com.pm.farm_backend.Service.NotificationService;
import com.pm.farm_backend.enums.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Create a notification", description = "Create a new notification for a user")
    public ResponseEntity<NotificationDto> createNotification(@RequestBody CreateNotificationDto createDto) {
        NotificationDto notification = notificationService.createNotification(createDto);
        return ResponseEntity.ok(notification);
    }

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get all notifications for the authenticated user")
    public ResponseEntity<NotificationResponseDto> getNotifications(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        NotificationResponseDto response = notificationService.getNotificationsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Get all unread notifications for the authenticated user")
    public ResponseEntity<NotificationResponseDto> getUnreadNotifications(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        NotificationResponseDto response = notificationService.getUnreadNotifications(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get the count of unread notifications")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<NotificationDto> markAsRead(
            @PathVariable Long id,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        NotificationDto notification = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for the user")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable Long id,
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences", description = "Get notification preferences for the user")
    public ResponseEntity<List<NotificationPreferenceDto>> getPreferences(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        List<NotificationPreferenceDto> preferences = notificationService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/preferences/{type}")
    @Operation(summary = "Update notification preference", description = "Update notification preference for a specific type")
    public ResponseEntity<NotificationPreferenceDto> updatePreference(
            @PathVariable NotificationType type,
            @Parameter(description = "User ID") @RequestParam Long userId,
            @RequestBody NotificationPreferenceDto preferenceDto) {
        
        NotificationPreferenceDto preference = notificationService.updatePreference(userId, type, preferenceDto);
        return ResponseEntity.ok(preference);
    }

    @PostMapping("/preferences/defaults")
    @Operation(summary = "Create default preferences", description = "Create default notification preferences for a user")
    public ResponseEntity<Map<String, String>> createDefaultPreferences(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        
        notificationService.createDefaultPreferences(userId);
        return ResponseEntity.ok(Map.of("message", "Default preferences created"));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Send bulk notifications", description = "Send notifications to multiple users")
    public ResponseEntity<Map<String, String>> sendBulkNotifications(
            @RequestParam List<Long> userIds,
            @RequestBody CreateNotificationDto notificationDto) {
        
        notificationService.sendBulkNotifications(userIds, notificationDto);
        return ResponseEntity.ok(Map.of("message", "Bulk notifications sent successfully"));
    }
}

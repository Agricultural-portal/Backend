package com.pm.farm_backend.Dto;

import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private String data;
    private Boolean isRead;
    private NotificationPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime readAt;
}

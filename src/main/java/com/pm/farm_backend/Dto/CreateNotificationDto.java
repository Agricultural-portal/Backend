package com.pm.farm_backend.Dto;

import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDto {
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private String data;
    private NotificationPriority priority;
}

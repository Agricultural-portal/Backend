package com.pm.farm_backend.Service;

import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Dto.NotificationDto;
import com.pm.farm_backend.Dto.NotificationPreferenceDto;
import com.pm.farm_backend.Dto.NotificationResponseDto;
import com.pm.farm_backend.enums.NotificationType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    NotificationDto createNotification(CreateNotificationDto createNotificationDto);

    NotificationResponseDto getNotificationsByUser(Long userId, Pageable pageable);

    NotificationResponseDto getUnreadNotifications(Long userId, Pageable pageable);

    Long getUnreadCount(Long userId);

    NotificationDto markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId, Long userId);

    void deleteExpiredNotifications();

    List<NotificationPreferenceDto> getUserPreferences(Long userId);

    NotificationPreferenceDto updatePreference(Long userId, NotificationType type, NotificationPreferenceDto preferenceDto);

    void createDefaultPreferences(Long userId);

    void sendBulkNotifications(List<Long> userIds, CreateNotificationDto notificationDto);
}

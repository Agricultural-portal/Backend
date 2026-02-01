package com.pm.farm_backend.Service;

import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Dto.NotificationDto;
import com.pm.farm_backend.Dto.NotificationPreferenceDto;
import com.pm.farm_backend.Dto.NotificationResponseDto;
import com.pm.farm_backend.Model.Notification;
import com.pm.farm_backend.Model.NotificationPreference;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.NotificationPreferenceRepository;
import com.pm.farm_backend.Repositories.NotificationRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationDto createNotification(CreateNotificationDto createDto) {
        User user = userRepository.findById(createDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(createDto.getType())
                .title(createDto.getTitle())
                .message(createDto.getMessage())
                .data(createDto.getData())
                .priority(createDto.getPriority() != null ? createDto.getPriority() : NotificationPriority.MEDIUM)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Created notification {} for user {}", saved.getId(), user.getId());

        return mapToDto(saved);
    }

    @Override
    public NotificationResponseDto getNotificationsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Notification> notificationPage = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        Long unreadCount = notificationRepository.countByUserAndIsRead(user, false);

        List<NotificationDto> notifications = notificationPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return NotificationResponseDto.builder()
                .notifications(notifications)
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .currentPage(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    public NotificationResponseDto getUnreadNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Notification> notificationPage = notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false, pageable);

        List<NotificationDto> notifications = notificationPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return NotificationResponseDto.builder()
                .notifications(notifications)
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .currentPage(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .unreadCount(notificationPage.getTotalElements())
                .build();
    }

    @Override
    public Long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Override
    @Transactional
    public NotificationDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        Notification updated = notificationRepository.save(notification);

        log.info("Marked notification {} as read for user {}", notificationId, userId);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationRepository.markAllAsReadByUser(user, LocalDateTime.now());
        log.info("Marked all notifications as read for user {}", userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notificationRepository.delete(notification);
        log.info("Deleted notification {} for user {}", notificationId, userId);
    }

    @Override
    @Transactional
    public void deleteExpiredNotifications() {
        notificationRepository.deleteExpiredNotifications(LocalDateTime.now());
        log.info("Deleted expired notifications");
    }

    @Override
    public List<NotificationPreferenceDto> getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationPreference> preferences = preferenceRepository.findByUser(user);

        // If no preferences exist, create defaults
        if (preferences.isEmpty()) {
            createDefaultPreferences(userId);
            preferences = preferenceRepository.findByUser(user);
        }

        return preferences.stream()
                .map(this::mapPreferenceToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationPreferenceDto updatePreference(Long userId, NotificationType type, NotificationPreferenceDto preferenceDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationPreference preference = preferenceRepository.findByUserAndType(user, type)
                .orElse(NotificationPreference.builder()
                        .user(user)
                        .type(type)
                        .build());

        preference.setEnableInApp(preferenceDto.getEnableInApp());
        preference.setEnableEmail(preferenceDto.getEnableEmail());
        preference.setEnableSms(preferenceDto.getEnableSms());

        NotificationPreference saved = preferenceRepository.save(preference);
        log.info("Updated notification preference for user {} and type {}", userId, type);

        return mapPreferenceToDto(saved);
    }

    @Override
    @Transactional
    public void createDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationPreference> preferences = new ArrayList<>();
        for (NotificationType type : NotificationType.values()) {
            NotificationPreference pref = NotificationPreference.builder()
                    .user(user)
                    .type(type)
                    .enableInApp(true)
                    .enableEmail(false)
                    .enableSms(false)
                    .build();
            preferences.add(pref);
        }

        preferenceRepository.saveAll(preferences);
        log.info("Created default notification preferences for user {}", userId);
    }

    @Override
    @Transactional
    public void sendBulkNotifications(List<Long> userIds, CreateNotificationDto notificationDto) {
        List<Notification> notifications = new ArrayList<>();

        for (Long userId : userIds) {
            try {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    Notification notification = Notification.builder()
                            .user(user)
                            .type(notificationDto.getType())
                            .title(notificationDto.getTitle())
                            .message(notificationDto.getMessage())
                            .data(notificationDto.getData())
                            .priority(notificationDto.getPriority() != null ? notificationDto.getPriority() : NotificationPriority.MEDIUM)
                            .isRead(false)
                            .build();
                    notifications.add(notification);
                }
            } catch (Exception e) {
                log.error("Error creating notification for user {}: {}", userId, e.getMessage());
            }
        }

        notificationRepository.saveAll(notifications);
        log.info("Sent bulk notifications to {} users", notifications.size());
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .data(notification.getData())
                .isRead(notification.getIsRead())
                .priority(notification.getPriority())
                .createdAt(notification.getCreatedAt())
                .expiresAt(notification.getExpiresAt())
                .readAt(notification.getReadAt())
                .build();
    }

    private NotificationPreferenceDto mapPreferenceToDto(NotificationPreference preference) {
        return NotificationPreferenceDto.builder()
                .id(preference.getId())
                .userId(preference.getUser().getId())
                .type(preference.getType())
                .enableInApp(preference.getEnableInApp())
                .enableEmail(preference.getEnableEmail())
                .enableSms(preference.getEnableSms())
                .build();
    }
}

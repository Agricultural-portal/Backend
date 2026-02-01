package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Notification;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead, Pageable pageable);

    Long countByUserAndIsRead(User user, Boolean isRead);

    List<Notification> findByUserAndExpiresAtBefore(User user, LocalDateTime expiresAt);

    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, NotificationType type);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user, @Param("readAt") LocalDateTime readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    void deleteExpiredNotifications(@Param("now") LocalDateTime now);
}

package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.NotificationPreference;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByUser(User user);

    Optional<NotificationPreference> findByUserAndType(User user, NotificationType type);

    void deleteByUser(User user);
}

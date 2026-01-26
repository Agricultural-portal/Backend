package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    java.util.List<User> findByRoleAndStatus(com.pm.farm_backend.enums.Role role,
            com.pm.farm_backend.enums.AccountStatus status);

    java.util.List<User> findByRole(com.pm.farm_backend.enums.Role role);

    long countByRole(com.pm.farm_backend.enums.Role role);

    long countByRoleAndStatus(com.pm.farm_backend.enums.Role role,
            com.pm.farm_backend.enums.AccountStatus status);

    long countByRoleAndCreatedAtAfter(com.pm.farm_backend.enums.Role role,
            java.time.LocalDateTime date);
}

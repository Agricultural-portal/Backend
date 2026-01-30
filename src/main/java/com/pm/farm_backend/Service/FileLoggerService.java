package com.pm.farm_backend.Service;

public interface FileLoggerService {

    /**
     * Log a successful registration
     */
    void logRegistrationSuccess(String email, String role);

    /**
     * Log a failed registration
     */
    void logRegistrationFailure(String email, String role, String failureReason);

    /**
     * Log a successful login
     */
    void logLoginSuccess(String email);

    /**
     * Log a failed login
     */
    void logLoginFailure(String email, String failureReason);

    /**
     * Log a successful logout
     */
    void logLogout(String email);

    /**
     * Log a user deletion
     */
    void logDeletion(String email, String deletedBy);
}

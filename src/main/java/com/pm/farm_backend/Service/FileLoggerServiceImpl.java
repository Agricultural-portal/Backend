package com.pm.farm_backend.Service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileLoggerServiceImpl implements FileLoggerService {

    private static final String LOG_DIRECTORY = "logs/user-activity";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FileLoggerServiceImpl() {
        ensureLogDirectoryExists();
    }

    @Override
    @Async
    public void logRegistrationSuccess(String email, String role) {
        String logEntry = String.format(
                "[%s] User registration successful with email %s having role %s",
                getCurrentTimestamp(),
                email,
                role);
        writeToLogFile(logEntry);
    }

    @Override
    @Async
    public void logRegistrationFailure(String email, String role, String failureReason) {
        String logEntry = String.format(
                "[%s] User registration failed with email %s having role %s - Reason: %s",
                getCurrentTimestamp(),
                email,
                role,
                failureReason);
        writeToLogFile(logEntry);
    }

    @Override
    @Async
    public void logLoginSuccess(String email) {
        String logEntry = String.format(
                "[%s] User login successful with email %s",
                getCurrentTimestamp(),
                email);
        writeToLogFile(logEntry);
    }

    @Override
    @Async
    public void logLoginFailure(String email, String failureReason) {
        String logEntry = String.format(
                "[%s] User login failed with email %s - Reason: %s",
                getCurrentTimestamp(),
                email,
                failureReason);
        writeToLogFile(logEntry);
    }

    @Override
    @Async
    public void logLogout(String email) {
        String logEntry = String.format(
                "[%s] User logout successful with email %s",
                getCurrentTimestamp(),
                email);
        writeToLogFile(logEntry);
    }

    @Override
    @Async
    public void logDeletion(String email, String deletedBy) {
        String logEntry = String.format(
                "[%s] User deletion successful with email %s deleted by %s",
                getCurrentTimestamp(),
                email,
                deletedBy);
        writeToLogFile(logEntry);
    }

    /**
     * Write log entry to file
     */
    private synchronized void writeToLogFile(String logEntry) {
        String logFileName = getCurrentLogFileName();
        File logFile = new File(LOG_DIRECTORY, logFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logEntry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get current timestamp as formatted string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    /**
     * Get current log file name based on date
     */
    private String getCurrentLogFileName() {
        String date = LocalDate.now().format(DATE_FORMATTER);
        return "user-activity-" + date + ".txt";
    }

    /**
     * Ensure log directory exists
     */
    private void ensureLogDirectoryExists() {
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created log directory: " + LOG_DIRECTORY);
            } else {
                System.err.println("Failed to create log directory: " + LOG_DIRECTORY);
            }
        }
    }
}

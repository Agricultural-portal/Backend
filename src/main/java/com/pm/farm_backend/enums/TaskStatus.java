package com.pm.farm_backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    PENDING("pending"),
    IN_PROGRESS("in-progress"),
    COMPLETED("completed"),
    DELETED("deleted");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TaskStatus fromString(String value) {
        if (value == null) return TaskStatus.PENDING;
        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return TaskStatus.PENDING;
    }
}

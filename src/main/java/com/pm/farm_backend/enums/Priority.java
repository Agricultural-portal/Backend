package com.pm.farm_backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Priority {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String value;

    Priority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Priority fromString(String value) {
        if (value == null) return null;
        for (Priority priority : Priority.values()) {
            if (priority.value.equalsIgnoreCase(value) || priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown priority: " + value);
    }
}

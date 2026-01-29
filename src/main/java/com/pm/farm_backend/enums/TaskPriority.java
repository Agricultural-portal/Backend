package com.pm.farm_backend.enums;

public enum TaskPriority {
    @com.fasterxml.jackson.annotation.JsonProperty("high")
    HIGH,
    @com.fasterxml.jackson.annotation.JsonProperty("medium")
    MEDIUM,
    @com.fasterxml.jackson.annotation.JsonProperty("low")
    LOW
}

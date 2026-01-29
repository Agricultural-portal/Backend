package com.pm.farm_backend.enums;

public enum CropStatus {
    @com.fasterxml.jackson.annotation.JsonProperty("pending")
    PENDING,
    @com.fasterxml.jackson.annotation.JsonProperty("in-progress")
    IN_PROGRESS,
    @com.fasterxml.jackson.annotation.JsonProperty("completed")
    COMPLETED,
    @com.fasterxml.jackson.annotation.JsonProperty("deleted")
    DELETED
}

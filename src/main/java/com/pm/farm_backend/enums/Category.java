package com.pm.farm_backend.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Category {
    @JsonProperty("General")
    General,
    @JsonProperty("Crop Cycle")
    Crop_Cycle
}

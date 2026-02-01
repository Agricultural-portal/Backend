package com.pm.farm_backend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PriorityConverter implements AttributeConverter<Priority, String> {

    @Override
    public String convertToDatabaseColumn(Priority priority) {
        if (priority == null) {
            return null;
        }
        return priority.getValue();
    }

    @Override
    public Priority convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Priority.fromString(dbData);
    }
}

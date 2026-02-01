package com.pm.farm_backend.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {

    @Override
    public String convertToDatabaseColumn(TaskStatus status) {
        if (status == null) {
            return null;
        }
        return status.getValue();
    }

    @Override
    public TaskStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return TaskStatus.PENDING;
        }
        return TaskStatus.fromString(dbData);
    }
}

package com.example.crm.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusEnumConverter implements AttributeConverter<StatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(StatusEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public StatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            // Convert database string to uppercase to match standard Java Enums (e.g., 'New' -> 'NEW')
            return StatusEnum.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fallback for unknown statuses
            return StatusEnum.UNKNOWN;
        }
    }
}

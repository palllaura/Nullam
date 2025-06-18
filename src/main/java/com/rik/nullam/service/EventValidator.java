package com.rik.nullam.service;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.ValidationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventValidator {

    private static final int MAXIMUM_EVENT_INFO_LENGTH = 1000;

    /**
     * Validate if event fields are correct.
     * @param dto dto with event info.
     * @return validation result.
     */
    public ValidationResult validate(EventDto dto) {
        ValidationResult result = new ValidationResult();

        if (isNullOrBlank(dto.getName()) || dto.getTime() == null || isNullOrBlank(dto.getLocation())) {
            result.addError("One of the fields is missing or blank.");
        }
        if (dto.getTime() != null && dto.getTime().isBefore(LocalDateTime.now())) {
            result.addError("Event time cannot be in the past.");
        }
        if (dto.getAdditionalInfo() != null && dto.getAdditionalInfo().length() > MAXIMUM_EVENT_INFO_LENGTH) {
            result.addError("Additional info is too long.");
        }
        return result;
    }

    /**
     * Check if field is null or blank.
     * @param field string to check.
     * @return true if null or blank, else false.
     */
    private boolean isNullOrBlank(String field) {
        return field == null || field.isBlank();
    }
}

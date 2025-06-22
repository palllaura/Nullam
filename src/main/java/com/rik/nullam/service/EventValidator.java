package com.rik.nullam.service;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.ValidationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.rik.nullam.service.ValidationResultErrorConstants.INCORRECT_TIME;
import static com.rik.nullam.service.ValidationResultErrorConstants.INFO_TOO_LONG;
import static com.rik.nullam.service.ValidationResultErrorConstants.MISSING_OR_BLANK;

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
            result.addError(MISSING_OR_BLANK);
        }
        if (dto.getTime() != null && dto.getTime().isBefore(LocalDateTime.now())) {
            result.addError(INCORRECT_TIME);
        }
        if (dto.getAdditionalInfo() != null && dto.getAdditionalInfo().length() > MAXIMUM_EVENT_INFO_LENGTH) {
            result.addError(INFO_TOO_LONG);
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

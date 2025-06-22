package com.rik.nullam.service;

import com.rik.nullam.dto.CompanyParticipationDto;
import com.rik.nullam.dto.PersonParticipationDto;
import com.rik.nullam.dto.ValidationResult;

import com.rik.nullam.entity.participation.PaymentMethod;
import com.rik.nullam.repository.EventRepository;
import org.springframework.stereotype.Service;

import static com.rik.nullam.service.ValidationResultErrorConstants.EVENT_NOT_FOUND;
import static com.rik.nullam.service.ValidationResultErrorConstants.INFO_TOO_LONG;
import static com.rik.nullam.service.ValidationResultErrorConstants.INVALID_CODE_FORMAT;
import static com.rik.nullam.service.ValidationResultErrorConstants.INVALID_NUM_OF_PARTICIPANTS;
import static com.rik.nullam.service.ValidationResultErrorConstants.INVALID_PAYMENT;
import static com.rik.nullam.service.ValidationResultErrorConstants.MISSING_OR_BLANK;


@Service
public class ParticipationValidator {

    private static final String PERSONAL_CODE_REGEX = "^[1-8]\\d{10}$";
    private static final String COMPANY_CODE_REGEX = "^\\d{7,8}$";
    private static final int MAXIMUM_PERSON_INFO_LENGTH = 1500;
    private static final int MAXIMUM_COMPANY_INFO_LENGTH = 5000;

    private final EventRepository eventRepository;

    public ParticipationValidator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Validate if person participation info is correct.
     * @param dto info to validate.
     * @return validation result.
     */
    public ValidationResult validatePerson(PersonParticipationDto dto) {
        ValidationResult result = new ValidationResult();

        if (dto.getEventId() == null ||
                isNullOrBlank(dto.getPaymentMethod()) ||
                isNullOrBlank(dto.getPersonalCode()) ||
                isNullOrBlank(dto.getFirstName()) ||
                isNullOrBlank(dto.getLastName())
        ) {
            result.addError(MISSING_OR_BLANK);
            return result;
        }

        if (!dto.getPersonalCode().matches(PERSONAL_CODE_REGEX)) {
            result.addError(INVALID_CODE_FORMAT);
        }
        if (dto.getAdditionalInfo() != null && dto.getAdditionalInfo().length() > MAXIMUM_PERSON_INFO_LENGTH) {
            result.addError(INFO_TOO_LONG);
        }

        if (!eventRepository.existsById(dto.getEventId())) {
            result.addError(EVENT_NOT_FOUND);
        }

        try {
            PaymentMethod.valueOf(dto.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            result.addError(INVALID_PAYMENT);
        }

        return result;
    }

    /**
     * Validate if company participation info is correct.
     * @param dto info to validate.
     * @return validation result.
     */
    public ValidationResult validateCompany(CompanyParticipationDto dto) {
        ValidationResult result = new ValidationResult();

        if (dto.getEventId() == null ||
                isNullOrBlank(dto.getPaymentMethod()) ||
                isNullOrBlank(dto.getCompanyName()) ||
                isNullOrBlank(dto.getRegistryCode()) ||
                dto.getNumberOfParticipants() == null
        ) {
            result.addError(MISSING_OR_BLANK);
            return result;
        }

        if (!dto.getRegistryCode().matches(COMPANY_CODE_REGEX)) {
            result.addError(INVALID_CODE_FORMAT);
        }

        if (dto.getNumberOfParticipants() < 1) {
            result.addError(INVALID_NUM_OF_PARTICIPANTS);
        }

        if (dto.getAdditionalInfo() != null && dto.getAdditionalInfo().length() > MAXIMUM_COMPANY_INFO_LENGTH) {
            result.addError(INFO_TOO_LONG);
        }

        if (!eventRepository.existsById(dto.getEventId())) {
            result.addError(EVENT_NOT_FOUND);
        }

        try {
            PaymentMethod.valueOf(dto.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            result.addError(INVALID_PAYMENT);
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

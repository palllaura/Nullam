package com.rik.nullam;

import com.rik.nullam.dto.CompanyParticipationDto;
import com.rik.nullam.dto.PersonParticipationDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.service.ParticipationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ParticipationValidatorTest {

    private static final String INFO_TOO_LONG = "Additional info is longer than the allowed length.";
    private static final String MISSING_OR_BLANK = "One of the fields is missing or blank.";
    private static final String INVALID_CODE_FORMAT = "Code format is invalid.";

    private ParticipationValidator validator;
    private EventRepository eventRepository;

    private ValidationResult result;
    private PersonParticipationDto personDto;
    private CompanyParticipationDto companyDto;


    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        validator = new ParticipationValidator(eventRepository);
        result = new ValidationResult();
        personDto = new PersonParticipationDto();
        companyDto = new CompanyParticipationDto();

        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");
        personDto.setPersonalCode("38806170123");
        personDto.setEventId(5L);
        personDto.setPaymentMethod("BANK_TRANSFER");
        personDto.setAdditionalInfo("Tuleb autoga.");

        companyDto.setCompanyName("Raamatuklubi MTÜ");
        companyDto.setRegistryCode("18882936");
        companyDto.setEventId(5L);
        companyDto.setPaymentMethod("BANK_TRANSFER");
        companyDto.setNumberOfParticipants(5);
        companyDto.setAdditionalInfo("Üks osaline tuleb koos koeraga.");
    }

    @Test
    void testValidateNewPersonParticiptionCorrect() {
        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validatePerson(personDto);
        Assertions.assertTrue(result.isValid());
    }


    @Test
    void testValidatePersonFailsIfFirstNameIsMissing() {
        personDto.setFirstName(null);

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidatePersonFailsIfLastNameIsBlank() {
        personDto.setLastName(" ");

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeIsMissing() {
        personDto.setPersonalCode(null);

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeIncludesLetters() {
        personDto.setPersonalCode("A8806170123");

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(INVALID_CODE_FORMAT));
    }

    @Test
    void validateCompanyCorrect() {
        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validateCompany(companyDto);
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void validateCompanyFailsNameIsBlank() {
        companyDto.setCompanyName(" ");

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validateCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void validateCompanyFailsCodeIsMissing() {
        companyDto.setRegistryCode(null);

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validateCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void validateCompanyFailsCodeIncludesSpecialCharacter() {
        companyDto.setRegistryCode("188-2936");

        when(eventRepository.existsById(5L)).thenReturn(true);
        result = validator.validateCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(INVALID_CODE_FORMAT));
    }
}

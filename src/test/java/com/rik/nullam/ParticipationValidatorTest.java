package com.rik.nullam;

import com.rik.nullam.dto.CompanyParticipationDto;
import com.rik.nullam.dto.PersonParticipationDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.participation.PaymentMethod;
import com.rik.nullam.service.ParticipationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ParticipationValidatorTest {

    private static final String INFO_TOO_LONG = "Additional info is longer than the allowed length.";
    private static final String MISSING_OR_BLANK = "One of the fields is missing or blank.";
    private static final String INVALID_CODE_FORMAT = "Code format is invalid.";

    ParticipationValidator validator;

    private ValidationResult result;
    private PersonParticipationDto personDto;
    private CompanyParticipationDto companyDto;


    @BeforeEach
    void setUp() {
        validator = new ParticipationValidator();
        result = new ValidationResult();
        personDto = new PersonParticipationDto();
        companyDto = new CompanyParticipationDto();

        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");
        personDto.setPersonalCode("38806170123");
        personDto.setEventId(5L);
        personDto.setPaymentMethod(PaymentMethod.BANK_TRANSFER.getDisplayName());
        personDto.setAdditionalInfo("Tuleb autoga.");

        companyDto.setCompanyName("Raamatuklubi MTÜ");
        companyDto.setRegistrationCode("18882936");
        companyDto.setEventId(5L);
        companyDto.setPaymentMethod(PaymentMethod.BANK_TRANSFER.getDisplayName());
        companyDto.setNumberOfParticipants(5);
        companyDto.setAdditionalInfo("Üks osaline tuleb koos koeraga.");
    }

    @Test
    void testValidateNewPersonParticiptionCorrect() {
result = validator.validatePerson(personDto);
        Assertions.assertTrue(result.isValid());
    }


    @Test
    void testValidatePersonFailsIfFirstNameIsMissing() {
        personDto.setFirstName(null);

        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidatePersonFailsIfLastNameIsBlank() {
        personDto.setLastName(" ");

        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeIsMissing() {
        personDto.setPersonalCode(null);

        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeIncludesLetters() {
        personDto.setPersonalCode("A8806170123");

        result = validator.validatePerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(INVALID_CODE_FORMAT));
    }

    @Test
    void validateCompanyCorrect() {
        result = validator.validateCompany(companyDto);
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void validateCompanyFailsNameIsBlank() {
        companyDto.setCompanyName(" ");

        result = validator.validateCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void validateCompanyFailsCodeIsMissing() {
        companyDto.setRegistrationCode(null);

        result = validator.validateCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void validateCompanyFailsCodeIncludesSpecialCharacter() {
        companyDto.setRegistrationCode("188-2936");
        result = validator.validateCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(INVALID_CODE_FORMAT));
    }
}

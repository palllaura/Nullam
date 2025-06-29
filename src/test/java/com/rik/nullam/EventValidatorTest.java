package com.rik.nullam;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.service.EventValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.rik.nullam.service.ValidationResultErrorConstants.INCORRECT_TIME;
import static com.rik.nullam.service.ValidationResultErrorConstants.INFO_TOO_LONG;
import static com.rik.nullam.service.ValidationResultErrorConstants.MISSING_OR_BLANK;


@SpringBootTest
class EventValidatorTest {

    private EventValidator validator;
    private ValidationResult result;
    private EventDto eventDto;


    @BeforeEach
    void setUp() {
        validator = new EventValidator();
        result = new ValidationResult();
        eventDto = new EventDto();
    }

    @Test
    void testValidateEventCorrect() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));

        result = validator.validate(eventDto);
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void validateEventNameIsNullInvalid() {
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("Some info");

        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }


    @Test
    void validateEventNameIsBlankInvalid() {
        eventDto.setName("     ");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("Some info");

        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }


    @Test
    void testValidateEventTimeIsNullInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidateEventLocationIsNullInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidateEventLocationIsBlankInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setLocation(" ");
        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(MISSING_OR_BLANK));
    }

    @Test
    void testValidateEventTimeIsInThePastInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setTime(LocalDateTime.now().minusDays(1));
        eventDto.setLocation("Tallinn");
        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(INCORRECT_TIME));
    }

    @Test
    void testValidateEventInfoIsTooLongInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("a".repeat(1001));

        result = validator.validate(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains(INFO_TOO_LONG));
    }
}

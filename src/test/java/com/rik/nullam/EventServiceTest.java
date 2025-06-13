package com.rik.nullam;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.event.Event;
import com.rik.nullam.repository.CompanyRepository;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.repository.ParticipationRepository;
import com.rik.nullam.repository.PersonRepository;
import com.rik.nullam.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EventServiceTest {

    private EventService service;

    private EventRepository eventRepository;
    private CompanyRepository companyRepository;
    private PersonRepository personRepository;
    private ParticipationRepository participationRepository;

    private ValidationResult result;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        companyRepository = mock(CompanyRepository.class);
        personRepository = mock(PersonRepository.class);
        participationRepository = mock(ParticipationRepository.class);

        service = new EventService(eventRepository, companyRepository, personRepository, participationRepository);

        result = new ValidationResult();
        eventDto = new EventDto();
    }

    @Test
    void testValidateEventCorrect() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));

        result = service.createEvent(eventDto);
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void testCreateEventCorrectEventTriggersSavedInRepository() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));

        service.createEvent(eventDto);

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEventCorrectEventSavesCorrectValues() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("Some info");

        service.createEvent(eventDto);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());

        Event savedEvent = eventCaptor.getValue();
        Assertions.assertEquals("Prügikoristuspäev", savedEvent.getName());
        Assertions.assertEquals("Tallinn", savedEvent.getLocation());
        Assertions.assertEquals(eventDto.getTime(), savedEvent.getTime());
        Assertions.assertEquals("Some info", savedEvent.getAdditionalInfo());
    }

    @Test
    void validateEventNameIsNullInvalid() {
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("Some info");

        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("One of the fields is missing or blank."));
    }


    @Test
    void validateEventNameIsBlankInvalid() {
        eventDto.setName("     ");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("Some info");

        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("One of the fields is missing or blank."));
    }


    @Test
    void testValidateEventTimeIsNullInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("One of the fields is missing or blank."));
    }

    @Test
    void testValidateEventLocationIsNullInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("One of the fields is missing or blank."));
    }

    @Test
    void testValidateEventLocationIsBlankInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setLocation(" ");
        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("One of the fields is missing or blank."));
    }

    @Test
    void testValidateEventTimeIsInThePastInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setTime(LocalDateTime.now().minusDays(1));
        eventDto.setLocation("Tallinn");
        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Event time cannot be in the past."));
    }

    @Test
    void testValidateEventInfoIsTooLongInvalid() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("a".repeat(1001));

        result = service.createEvent(eventDto);

        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Additional info is longer than the allowed length."));
    }

}

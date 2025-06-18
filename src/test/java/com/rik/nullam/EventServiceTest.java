package com.rik.nullam;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.ParticipationSummaryDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.event.Event;

import com.rik.nullam.entity.participation.CompanyParticipation;
import com.rik.nullam.entity.participation.PaymentMethod;
import com.rik.nullam.entity.participation.PersonParticipation;
import com.rik.nullam.repository.CompanyParticipationRepository;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.repository.PersonParticipationRepository;
import com.rik.nullam.service.EventService;
import com.rik.nullam.service.EventValidator;
import com.rik.nullam.service.ParticipationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class EventServiceTest {

    private EventService service;

    private EventValidator eventValidator;
    private ParticipationValidator participationValidator;

    private EventRepository eventRepository;
    private PersonParticipationRepository personRepository;
    private CompanyParticipationRepository companyRepository;

    private EventDto eventDto;

    private Event event;

    @BeforeEach
    void setUp() {
        eventValidator = mock(EventValidator.class);
        participationValidator = mock(ParticipationValidator.class);

        eventRepository = mock(EventRepository.class);
        companyRepository = mock(CompanyParticipationRepository.class);
        personRepository = mock(PersonParticipationRepository.class);

        service = new EventService(eventValidator, participationValidator,
                eventRepository, companyRepository, personRepository);

        eventDto = new EventDto();

        event = new Event("Prügikoristuspäev", LocalDateTime.now().plusDays(1),
                "Tallinn", null);
    }

    @Test
    void testCreateEventCorrectEventTriggersSavedInRepository() {
        when(eventValidator.validate(eventDto)).thenReturn(new ValidationResult());
        service.createEvent(eventDto);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEventCorrectEventSavesCorrectValues() {
        eventDto.setName("Prügikoristuspäev");
        eventDto.setLocation("Tallinn");
        eventDto.setTime(LocalDateTime.now().plusDays(1));
        eventDto.setAdditionalInfo("Some info");

        when(eventValidator.validate(eventDto)).thenReturn(new ValidationResult());
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
    void testDeleteEventSuccessfulTrue() {
        when(eventRepository.findEventById(5L)).thenReturn(Optional.of(event));
        Assertions.assertTrue(service.deleteEventById(5L));
    }


    @Test
    void testDeleteEventSuccessfulDeletesEvent() {
        when(eventRepository.findEventById(5L)).thenReturn(Optional.of(event));
        service.deleteEventById(5L);
        verify(eventRepository, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteEventNoEventWithIdFalse() {
        when(eventRepository.findEventById(5L)).thenReturn(Optional.empty());
        Assertions.assertFalse(service.deleteEventById(5L));
    }

    @Test
    void testDeleteEventInThePastFalse() {
        Event pastEvent = new Event(
                "Linnajooks", LocalDateTime.now().minusDays(1L), "Pärnu", "5 km");
        when(eventRepository.findEventById(5L)).thenReturn(Optional.of(pastEvent));
        Assertions.assertFalse(service.deleteEventById(5L));
    }


    @Test
    void testGetFutureEventsSummariesTriggersCorrectMethodInRepository() {
        service.getFutureEventsSummaries();

        verify(eventRepository, times(1)).findEventsByTimeAfter(any());
    }

    @Test
    void testGetPastEventsSummariesTriggersCorrectMethodInRepository() {
        service.getPastEventsSummaries();

        verify(eventRepository, times(1)).findEventsByTimeBefore(any());
    }

    @Test
    void testCalculateNumberOfParticipantsOnlyCompaniesCorrectAmount() {
        CompanyParticipation participation1 = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);

        CompanyParticipation participation2 = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Kanakasvatus OÜ", "7891234", 12);


        when(eventRepository.findEventsByTimeAfter(any())).thenReturn(List.of(event));
        when(companyRepository.getCompanyParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation1, participation2));
        when(personRepository.getPersonParticipationsByEvent_Id(any()))
                .thenReturn(List.of());
        List<EventSummaryDto> summaries = service.getFutureEventsSummaries();
        Assertions.assertEquals(20, summaries.get(0).getNumberOfParticipants());
    }

    @Test
    void testCalculateNumberOfParticipantsPersonsAndCompaniesCorrectAmount() {
        CompanyParticipation participation1 = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);
        CompanyParticipation participation2 = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Kanakasvatus OÜ", "7891234", 12);
        PersonParticipation participation3 = new PersonParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Mari", "Mets", "4880101376");
        PersonParticipation participation4 = new PersonParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Mati", "Mets", "3880101376");

        when(eventRepository.findEventsByTimeAfter(any())).thenReturn(List.of(event));
        when(companyRepository.getCompanyParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation1, participation2));
        when(personRepository.getPersonParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation3, participation4));
        List<EventSummaryDto> summaries = service.getFutureEventsSummaries();
        Assertions.assertEquals(22, summaries.get(0).getNumberOfParticipants());
    }

    @Test
    void testGetEventSummariesListIncludesCorrectInfo() {
        CompanyParticipation participation1 = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);
        PersonParticipation participation2 = new PersonParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Mari", "Mets", "4880101376");

        when(eventRepository.findEventsByTimeAfter(any())).thenReturn(List.of(event));
        when(companyRepository.getCompanyParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation1));
        when(personRepository.getPersonParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation2));
        List<EventSummaryDto> summaries = service.getFutureEventsSummaries();
        EventSummaryDto summary = summaries.get(0);

        Assertions.assertEquals("Prügikoristuspäev", summary.getName());
        Assertions.assertEquals("Tallinn", summary.getLocation());
        Assertions.assertEquals(event.getTime(), summary.getTime());
        Assertions.assertEquals(9, summary.getNumberOfParticipants());
    }


    @Test
    void testGetEventParticipantSummariesListCorrectDataForPersons() {
        PersonParticipation participation = new PersonParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Mari", "Mets", "4880101376");
        participation.setId(5L);

        when(companyRepository.getCompanyParticipationsByEvent_Id(any()))
                .thenReturn(List.of());
        when(personRepository.getPersonParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation));
        List<ParticipationSummaryDto> summaries = service.getEventParticipantSummariesList(5L);
        ParticipationSummaryDto summaryDto = summaries.get(0);

        Assertions.assertEquals("Mari Mets", summaryDto.getName());
        Assertions.assertEquals("4880101376", summaryDto.getIdCode());
        Assertions.assertEquals(5L, summaryDto.getParticipationId());
    }

    @Test
    void testGetEventParticipantSummariesListCorrectDataForCompanies() {
        CompanyParticipation participation = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);
        participation.setId(5L);

        when(companyRepository.getCompanyParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation));
        when(personRepository.getPersonParticipationsByEvent_Id(any()))
                .thenReturn(List.of());
        List<ParticipationSummaryDto> summaries = service.getEventParticipantSummariesList(5L);
        ParticipationSummaryDto summaryDto = summaries.get(0);

        Assertions.assertEquals("Maalritööd OÜ", summaryDto.getName());
        Assertions.assertEquals("123456", summaryDto.getIdCode());
        Assertions.assertEquals(5L, summaryDto.getParticipationId());
    }

    @Test
    void testDeletePersonParticipationCorrectIsDeleted() {
        PersonParticipation participation = new PersonParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Mari", "Mets", "4880101376");
        participation.setId(5L);
        when(personRepository.findById(5L)).thenReturn(Optional.of(participation));

        service.deleteParticipation(ParticipationSummaryDto.ParticipationType.PERSON, 5L);
        verify(personRepository, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteCompanyParticipationCorrectIsDeleted() {
        CompanyParticipation participation = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);
        participation.setId(5L);
        when(companyRepository.findById(5L)).thenReturn(Optional.of(participation));

        service.deleteParticipation(ParticipationSummaryDto.ParticipationType.COMPANY, 5L);
        verify(companyRepository, times(1)).deleteById(5L);
    }

    @Test
    void testDeleteCompanyParticipationEventIsInThePastFalse() {
        event = new Event("Prügikoristuspäev", LocalDateTime.now().minusDays(1),
                "Tallinn", null);

        CompanyParticipation participation = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);
        participation.setId(5L);
        when(companyRepository.findById(5L)).thenReturn(Optional.of(participation));

        Assertions.assertFalse(service.deleteParticipation(
                ParticipationSummaryDto.ParticipationType.COMPANY, 5L));
    }

    @Test
    void testDeleteCompanyParticipationParticipationNotFoundFalse() {
        CompanyParticipation participation = new CompanyParticipation(event, PaymentMethod.BANK_TRANSFER,
                "Some info", "Maalritööd OÜ", "123456", 8);
        participation.setId(5L);
        when(companyRepository.findById(5L)).thenReturn(Optional.empty());

        Assertions.assertFalse(service.deleteParticipation(
                ParticipationSummaryDto.ParticipationType.COMPANY, 5L));
    }


}

package com.rik.nullam;

import com.rik.nullam.dto.CompanyDto;
import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.ParticipantSummaryDto;
import com.rik.nullam.dto.PersonDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.event.Event;
import com.rik.nullam.entity.participant.Company;
import com.rik.nullam.entity.participant.Person;
import com.rik.nullam.entity.participation.Participation;
import com.rik.nullam.entity.participation.PaymentMethod;
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

    private EventRepository eventRepository;
    private CompanyRepository companyRepository;
    private PersonRepository personRepository;
    private ParticipationRepository participationRepository;

    private ValidationResult result;
    private PersonDto personDto;
    private CompanyDto companyDto;
    private EventDto eventDto;

    private Event event;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        companyRepository = mock(CompanyRepository.class);
        personRepository = mock(PersonRepository.class);
        participationRepository = mock(ParticipationRepository.class);

        service = new EventService(eventRepository, companyRepository, personRepository, participationRepository);

        result = new ValidationResult();
        personDto = new PersonDto();
        companyDto = new CompanyDto();
        eventDto = new EventDto();

        event = new Event("Prügikoristuspäev", LocalDateTime.now().plusDays(1),
                "Tallinn", null);
    }

    @Test
    void testValidateNewPersonCorrect() {
        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");
        personDto.setPersonalCode("38806170123");

        when(personRepository.existsPersonByPersonalCode("38806170123")).thenReturn(false);

        result = service.addPerson(personDto);
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void testAddNewPersonCorrectTriggersSaveInRepository() {
        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");
        personDto.setPersonalCode("38806170123");

        when(personRepository.existsPersonByPersonalCode("38806170123")).thenReturn(false);

        result = service.addPerson(personDto);
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testValidatePersonFailsIfFirstNameIsMissing() {
        personDto.setLastName("Haab");
        personDto.setPersonalCode("38806170123");

        when(personRepository.existsPersonByPersonalCode("38806170123")).thenReturn(false);

        result = service.addPerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("First name is missing or blank."));
    }

    @Test
    void testValidatePersonFailsIfLastNameIsBlank() {
        personDto.setFirstName("Hugo");
        personDto.setLastName(" ");
        personDto.setPersonalCode("38806170123");

        when(personRepository.existsPersonByPersonalCode("38806170123")).thenReturn(false);

        result = service.addPerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Last name is missing or blank."));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeIsMissing() {
        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");

        when(personRepository.existsPersonByPersonalCode(any())).thenReturn(false);

        result = service.addPerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Personal code is missing or blank."));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeIncludesLetters() {
        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");
        personDto.setPersonalCode("A8806170123");

        when(personRepository.existsPersonByPersonalCode("A8806170123")).thenReturn(false);

        result = service.addPerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Personal code does not match correct format."));
    }

    @Test
    void testValidatePersonFailsIfPersonalCodeAlreadyExists() {
        personDto.setFirstName("Hugo");
        personDto.setLastName("Haab");
        personDto.setPersonalCode("38806170123");

        when(personRepository.existsPersonByPersonalCode("38806170123")).thenReturn(true);

        result = service.addPerson(personDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Person with this personal code already exists."));
    }


    @Test
    void validateCompanyCorrect() {
        companyDto.setCompanyName("Raamatuklubi MTÜ");
        companyDto.setRegistryCode("18882936");

        when(companyRepository.existsCompanyByRegistryCode("18882936")).thenReturn(false);
        result = service.addCompany(companyDto);
        Assertions.assertTrue(result.isValid());
    }

    @Test
    void testAddNewCompanyCorrectTriggersSaveInRepository() {
        companyDto.setCompanyName("Raamatuklubi MTÜ");
        companyDto.setRegistryCode("18882936");

        when(companyRepository.existsCompanyByRegistryCode("18882936")).thenReturn(false);
        result = service.addCompany(companyDto);
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    void validateCompanyFailsNameIsBlank() {
        companyDto.setCompanyName(" ");
        companyDto.setRegistryCode("18882936");

        when(companyRepository.existsCompanyByRegistryCode("18882936")).thenReturn(false);
        result = service.addCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Company name is missing or blank."));
    }

    @Test
    void validateCompanyFailsCodeIsMissing() {
        companyDto.setCompanyName("Raamatuklubi MTÜ");

        when(companyRepository.existsCompanyByRegistryCode(any())).thenReturn(false);
        result = service.addCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Registry code is missing or blank."));
    }

    @Test
    void validateCompanyFailsCodeIncludesSpecialCharacter() {
        companyDto.setCompanyName("Raamatuklubi MTÜ");
        companyDto.setRegistryCode("188-2936");

        when(companyRepository.existsCompanyByRegistryCode("188-2936")).thenReturn(false);
        result = service.addCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Registry code does not match correct format."));
    }

    @Test
    void validateCompanyFailsCodeAlreadyExists() {
        companyDto.setCompanyName("Raamatuklubi MTÜ");
        companyDto.setRegistryCode("18882936");

        when(companyRepository.existsCompanyByRegistryCode("18882936")).thenReturn(true);
        result = service.addCompany(companyDto);
        Assertions.assertFalse(result.isValid());
        Assertions.assertTrue(result.getMessages().contains("Company with this registry code already exists."));
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
    void testDeleteEventSuccessfulTrue() {
        when(eventRepository.findEventById(5L)).thenReturn(Optional.of(event));
        Assertions.assertTrue(service.deleteEventById(5L));
    }

    @Test
    void testDeleteEventSuccessfulDeletesRelatedParticipations() {
        when(eventRepository.findEventById(5L)).thenReturn(Optional.of(event));
        service.deleteEventById(5L);
        verify(participationRepository, times(1)).deleteAllByEvent(event);
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
        Company company1 = new Company("Maalritööd OÜ", "123456");
        Company company2 = new Company("Kanakasvatus OÜ", "7891234");

        Participation participation1 = new Participation(
                event, company1, 8, PaymentMethod.BANK_TRANSFER, null
        );
        Participation participation2 = new Participation(
                event, company2, 12, PaymentMethod.BANK_TRANSFER, null
        );

        when(eventRepository.findEventsByTimeAfter(any())).thenReturn(List.of(event));
        when(participationRepository.getParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation1, participation2));
        List<EventSummaryDto> summaries = service.getFutureEventsSummaries();
        Assertions.assertEquals(20, summaries.get(0).getNumberOfParticipants());
    }

    @Test
    void testCalculateNumberOfParticipantsPersonsAndCompaniesCorrectAmount() {
        Person person1 = new Person("Mari", "Mets", "4880101376");
        Person person2 = new Person("Mati", "Mets", "3880101376");
        Company company1 = new Company("Maalritööd OÜ", "123456");
        Company company2 = new Company("Kanakasvatus OÜ", "7891234");

        Participation participation1 = new Participation(
                event, person1, 1, PaymentMethod.BANK_TRANSFER, null
        );
        Participation participation2 = new Participation(
                event, person2, 1, PaymentMethod.BANK_TRANSFER, null
        );

        Participation participation3 = new Participation(
                event, company1, 8, PaymentMethod.BANK_TRANSFER, null
        );
        Participation participation4 = new Participation(
                event, company2, 12, PaymentMethod.BANK_TRANSFER, null
        );

        when(eventRepository.findEventsByTimeAfter(any())).thenReturn(List.of(event));
        when(participationRepository.getParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation1, participation2, participation3, participation4));
        List<EventSummaryDto> summaries = service.getFutureEventsSummaries();
        Assertions.assertEquals(22, summaries.get(0).getNumberOfParticipants());
    }

    @Test
    void testGetEventSummariesListIncludesCorrectInfo() {
        Company company1 = new Company("Maalritööd OÜ", "123456");
        Person person1 = new Person("Mari", "Mets", "4880101376");

        Participation participation1 = new Participation(
                event, company1, 8, PaymentMethod.BANK_TRANSFER, null
        );
        Participation participation2 = new Participation(
                event, person1, 1, PaymentMethod.BANK_TRANSFER, null
        );

        when(eventRepository.findEventsByTimeAfter(any())).thenReturn(List.of(event));
        when(participationRepository.getParticipationsByEvent_Id(any()))
                .thenReturn(List.of(participation1, participation2));
        List<EventSummaryDto> summaries = service.getFutureEventsSummaries();
        EventSummaryDto summary = summaries.get(0);

        Assertions.assertEquals("Prügikoristuspäev", summary.getName());
        Assertions.assertEquals("Tallinn", summary.getLocation());
        Assertions.assertEquals(event.getTime(), summary.getTime());
        Assertions.assertEquals(9, summary.getNumberOfParticipants());
    }

    @Test
    void testGetEventParticipantSummariesListTriggersCorrectMethodInRepository() {
        service.getEventParticipantSummariesList(5L);
        verify(participationRepository, times(1)).getParticipationsByEvent_Id(5L);
    }

    @Test
    void testGetEventParticipantSummariesListCorrectDataForPersons() {
        Person person = new Person("Mati", "Mänd", "3881506248");
        Participation participation = new Participation(
                event, person, 1, PaymentMethod.BANK_TRANSFER, null);
        participation.setId(5L);

        when(participationRepository.getParticipationsByEvent_Id(5L)).thenReturn(List.of(participation));
        List<ParticipantSummaryDto> summaries = service.getEventParticipantSummariesList(5L);
        ParticipantSummaryDto summaryDto = summaries.get(0);

        Assertions.assertEquals("Mati Mänd", summaryDto.getName());
        Assertions.assertEquals("3881506248", summaryDto.getIdCode());
        Assertions.assertEquals(5L, summaryDto.getParticipationId());
    }

    @Test
    void testGetEventParticipantSummariesListCorrectDataForCompanies() {
        Company company = new Company("Floristika OÜ", "1234567");
        Participation participation = new Participation(
                event, company, 5, PaymentMethod.BANK_TRANSFER, null);
        participation.setId(5L);

        when(participationRepository.getParticipationsByEvent_Id(5L)).thenReturn(List.of(participation));
        List<ParticipantSummaryDto> summaries = service.getEventParticipantSummariesList(5L);
        ParticipantSummaryDto summaryDto = summaries.get(0);

        Assertions.assertEquals("Floristika OÜ", summaryDto.getName());
        Assertions.assertEquals("1234567", summaryDto.getIdCode());
        Assertions.assertEquals(5L, summaryDto.getParticipationId());
    }

    @Test
    void testRemoveParticipantFromEventTriggersCorrectMethodInRepository() {
        service.removeParticipantFromEvent(5L);
        verify(participationRepository, times(1)).getParticipationById(5L);
    }

    @Test
    void testRemoveParticipantFromEventIncorrectIOdResultsFalse() {
        when(participationRepository.getParticipationById(5L)).thenReturn(Optional.empty());
        Assertions.assertFalse(service.removeParticipantFromEvent(5L));
    }

    @Test
    void testRemoveParticipantFromEventParticipationIsDeleted() {
        Company company = new Company("Floristika OÜ", "1234567");
        Participation participation = new Participation(
                event, company, 5, PaymentMethod.BANK_TRANSFER, null);
        participation.setId(5L);

        when(participationRepository.getParticipationById(any())).thenReturn(Optional.of(participation));

        service.removeParticipantFromEvent(5L);
        verify(participationRepository, times(1)).deleteById(5L);
    }


}

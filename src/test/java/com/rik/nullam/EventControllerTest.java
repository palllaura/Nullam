package com.rik.nullam;

import com.rik.nullam.controller.EventController;
import com.rik.nullam.dto.CompanyDto;
import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.PersonDto;
import com.rik.nullam.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EventControllerTest {

    private EventController controller;
    private EventService service;


    private EventDto eventDto;
    private EventSummaryDto summaryDto;

    @BeforeEach
    void setUp() {
        service = mock(EventService.class);
        controller = new EventController(service);

        eventDto = new EventDto();
        summaryDto = new EventSummaryDto();
    }

    @Test
    void testAddPersonTriggersCorrectMethodInService() {
        controller.addPerson(new PersonDto());
        verify(service, times(1)).addPerson(any(PersonDto.class));
    }

    @Test
    void testAddCompanyTriggersCorrectMethodInService() {
        controller.addCompany(new CompanyDto());
        verify(service, times(1)).addCompany(any(CompanyDto.class));
    }

    @Test
    void testCreateEventTriggersCorrectMethodInService() {
        controller.createEvent(eventDto);
        verify(service, times(1)).createEvent(any(EventDto.class));
    }

    @Test
    void testDeleteEventTriggersCorrectMethodInService() {
        summaryDto.setId(1L);
        controller.deleteEvent(summaryDto.getId());
        verify(service, times(1)).deleteEventById(1L);
    }

    @Test
    void testGetPastEventsTriggersCorrectMethodInService() {
        controller.getPastEvents();
        verify(service, times(1)).getPastEventsSummaries();
    }

    @Test
    void testGetFutureEventsTriggersCorrectMethodInService() {
        controller.getFutureEvents();
        verify(service, times(1)).getFutureEventsSummaries();
    }

    @Test
    void testGetParticipantsForAnEventTriggersCorrectMethodInService() {
        controller.getEventParticipantsByEventId(5L);
        verify(service, times(1)).getEventParticipantSummariesList(5L);
    }

    @Test
    void testRemoveParticipantFromEventTriggersCorrectMethodInService() {
        controller.removeParticipantFromEvent(5L);
        verify(service, times(1)).removeParticipantFromEvent(5L);
    }


}

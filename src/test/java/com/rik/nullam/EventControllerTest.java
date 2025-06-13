package com.rik.nullam;

import com.rik.nullam.controller.EventController;
import com.rik.nullam.dto.EventDto;
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

    @BeforeEach
    void setUp() {
        service = mock(EventService.class);
        controller = new EventController(service);

        eventDto = new EventDto();
    }

    @Test
    void testCreateEventTriggersCorrectMethodInService() {
        controller.createEvent(eventDto);
        verify(service, times(1)).createEvent(any(EventDto.class));
    }

}

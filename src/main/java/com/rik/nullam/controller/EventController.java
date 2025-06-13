package com.rik.nullam.controller;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.service.EventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    /**
     * Event controller constructor.
     * @param eventService service.
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Add a new event.
     * @param eventDto dto with event info.
     * @return validation result.
     */
    @PostMapping("/newEvent")
    public ValidationResult createEvent(@RequestBody EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }
}

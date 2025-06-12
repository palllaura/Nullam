package com.rik.nullam.controller;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody @Valid EventDto eventDto) {
        boolean created = eventService.createEvent(eventDto);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Event created.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid event data.");
        }
    }
}


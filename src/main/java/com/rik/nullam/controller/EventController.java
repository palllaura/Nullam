package com.rik.nullam.controller;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.ParticipantSummaryDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.service.EventService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PostMapping("/create")
    public ValidationResult createEvent(@RequestBody EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    /**
     * Delete event by id.
     * @param id ID of event.
     * @return true if event was deleted, else false.
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteEvent(@PathVariable Long id) {
        return eventService.deleteEventById(id);
    }

    /**
     * Get summaries for all past events.
     * @return summaries in a list.
     */
    @GetMapping("/pastEvents")
    public List<EventSummaryDto> getPastEvents() {
        return eventService.getPastEventsSummaries();
    }

    /**
     * Get summaries for all future events.
     * @return summaries in a list.
     */
    @GetMapping("/futureEvents")
    public List<EventSummaryDto> getFutureEvents() {
        return eventService.getFutureEventsSummaries();
    }

    /**
     * Get summaries of all participants for an event.
     * @param eventId ID of the event.
     * @return summaries in a list.
     */
    @GetMapping("/participants/{eventId}")
    public List<ParticipantSummaryDto> getEventParticipantsByEventId(@PathVariable Long eventId) {
        return eventService.getEventParticipantSummariesList(eventId);
    }
}

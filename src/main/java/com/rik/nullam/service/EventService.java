package com.rik.nullam.service;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.event.Event;
import com.rik.nullam.entity.participation.Participation;
import com.rik.nullam.repository.CompanyRepository;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.repository.ParticipationRepository;
import com.rik.nullam.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Event service class to handle adding and removing events and participants.
 */
@Service
public class EventService {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(EventService.class.getName());
    private static final int MAXIMUM_EVENT_INFO_LENGTH = 1000;

    private final EventRepository eventRepository;
    private final CompanyRepository companyRepository;
    private final PersonRepository personRepository;
    private final ParticipationRepository participationRepository;

    /**
     * Event service constructor.
     *
     * @param eventRepository         event repository.
     * @param companyRepository       company repository.
     * @param personRepository        person repository.
     * @param participationRepository participant repository.
     */
    public EventService(EventRepository eventRepository,
                        CompanyRepository companyRepository,
                        PersonRepository personRepository,
                        ParticipationRepository participationRepository
    ) {
        this.eventRepository = eventRepository;
        this.companyRepository = companyRepository;
        this.personRepository = personRepository;
        this.participationRepository = participationRepository;
    }

    /**
     * Validate if event fields are correct. Save event if correct.
     *
     * @param eventDto event to save.
     * @return ValidationResult with relevant error messages.
     */
    public ValidationResult createEvent(EventDto eventDto) {
        ValidationResult validationResult = validateEvent(eventDto);

        if (!validationResult.isValid()) {
            return validationResult;
        }
        Event event = new Event(eventDto.getName(), eventDto.getTime(),
                eventDto.getLocation(), eventDto.getAdditionalInfo());
        eventRepository.save(event);
        LOGGER.info("Event successfully created: " + eventDto.getLocation() + " at " + eventDto.getTime());

        return validationResult;
    }

    /**
     * Validate event dto fields.
     * Fields cannot be missing or empty, event cannot be in the past and event info cannot be longer than allowed.
     *
     * @param eventDto event Dto to validate.
     * @return if all fields are valid, else false.
     */
    private ValidationResult validateEvent(EventDto eventDto) {
        ValidationResult validationResult = new ValidationResult();

        if (eventDto.getName() == null || eventDto.getName().isBlank() || eventDto.getTime() == null
                || eventDto.getLocation() == null || eventDto.getLocation().isBlank()) {
            String message = "One of the fields is missing or blank.";
            validationResult.addError(message);
            LOGGER.info(message);
        }

        if (eventDto.getTime() != null && eventDto.getTime().isBefore(LocalDateTime.now())) {
            String message = "Event time cannot be in the past.";
            validationResult.addError(message);
            LOGGER.info(message);
        }

        if (eventDto.getAdditionalInfo() != null && eventDto.getAdditionalInfo().length() > MAXIMUM_EVENT_INFO_LENGTH) {
            String message = "Additional info is longer than the allowed length.";
            validationResult.addError(message);
            LOGGER.info(message);
        }
        return validationResult;
    }

    /**
     * Get summaries for all future events including name, time, location and number of participants.
     *
     * @return summaries in a list.
     */
    public List<EventSummaryDto> getFutureEventsSummaries() {
        List<Event> futureEvents = eventRepository.findEventsByTimeAfter(LocalDateTime.now());
        return createEventSummariesList(futureEvents);
    }

    /**
     * Get summaries for all past events including name, time, location and number of participants.
     *
     * @return summaries in a list.
     */
    public List<EventSummaryDto> getPastEventsSummaries() {
        List<Event> pastEvents = eventRepository.findEventsByTimeBefore(LocalDateTime.now());
        return createEventSummariesList(pastEvents);
    }

    /**
     * Create a list of summaries from a list of events.
     *
     * @param events List of events.
     * @return summaries in a list.
     */
    private List<EventSummaryDto> createEventSummariesList(List<Event> events) {
        List<EventSummaryDto> result = new ArrayList<>();

        for (Event event : events) {
            EventSummaryDto summary = new EventSummaryDto();
            summary.setName(event.getName());
            summary.setTime(event.getTime());
            summary.setLocation(event.getLocation());

            int numOfParticipants = calculateNumberOfParticipants(event.getId());
            summary.setNumberOfParticipants(numOfParticipants);

            result.add(summary);
        }

        return result;
    }

    /**
     * Calculate the total amount of participants in an event.
     *
     * @param eventId id of the event.
     * @return total number of participants.
     */
    private int calculateNumberOfParticipants(Long eventId) {
        List<Participation> participations = participationRepository.getParticipationsByEvent_Id(eventId);
        int result = 0;

        for (Participation participation : participations) {
            result += participation.getNumberOfAttendees();
        }
        return result;
    }

}

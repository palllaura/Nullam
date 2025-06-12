package com.rik.nullam.service;

import com.rik.nullam.dto.EventDto;
import com.rik.nullam.entity.event.Event;
import com.rik.nullam.repository.CompanyRepository;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.repository.ParticipationRepository;
import com.rik.nullam.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
     * @param eventRepository event repository.
     * @param companyRepository company repository.
     * @param personRepository person repository.
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
     * @return true if event was saved, else false.
     */
    public boolean createEvent(EventDto eventDto) {
        if (!validateEvent(eventDto)) {
            return false;
        }
        Event event = new Event(eventDto.getTime(), eventDto.getLocation(), eventDto.getAdditionalInfo());
        eventRepository.save(event);
        LOGGER.info("Event successfully created: " + eventDto.getLocation() + " at " + eventDto.getTime());

        return true;
    }

    /**
     * Validate event dto fields.
     * Fields cannot be missing or empty, event cannot be in the past and event info cannot be longer than allowed.
     *
     * @param eventDto event Dto to validate.
     * @return true if all fields are valid, else false.
     */
    private boolean validateEvent(EventDto eventDto) {
        if (eventDto.getTime() == null || eventDto.getLocation() == null || eventDto.getLocation().isBlank()) {
            LOGGER.info("One of the fields is missing or blank.");
            return false;
        }

        if (eventDto.getTime().isBefore(LocalDateTime.now())) {
            LOGGER.info("Event time cannot be in the past.");
            return false;
        }

        if (eventDto.getAdditionalInfo() != null && eventDto.getAdditionalInfo().length() > MAXIMUM_EVENT_INFO_LENGTH) {
            LOGGER.info("Additional info is longer than the allowed length.");
            return false;
        }

        return true;
    }


}

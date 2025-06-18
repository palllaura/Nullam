package com.rik.nullam.service;

import com.rik.nullam.dto.CompanyParticipationDto;
import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.ParticipantSummaryDto;
import com.rik.nullam.dto.PersonParticipationDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.event.Event;

import com.rik.nullam.entity.participation.CompanyParticipation;
import com.rik.nullam.entity.participation.PaymentMethod;
import com.rik.nullam.entity.participation.PersonParticipation;
import com.rik.nullam.repository.CompanyParticipationRepository;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.repository.PersonParticipationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Event service class to handle adding and removing events and participants.
 */
@Service
public class EventService {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(EventService.class.getName());

    private final EventValidator eventValidator;
    private final ParticipationValidator participationValidator;

    private final EventRepository eventRepository;
    private final CompanyParticipationRepository companyParticipationRepository;
    private final PersonParticipationRepository personParticipationRepository;

    /**
     * Event service constructor.
     * @param eventValidator event validator.
     * @param participationValidator participation validator.
     * @param eventRepository event repository.
     * @param companyParticipationRepository company participation repository.
     * @param personParticipationRepository person participation repository.
     */
    public EventService(EventValidator eventValidator,
                        ParticipationValidator participationValidator,
                        EventRepository eventRepository,
                        CompanyParticipationRepository companyParticipationRepository,
                        PersonParticipationRepository personParticipationRepository
    ) {
        this.eventValidator = eventValidator;
        this.participationValidator = participationValidator;
        this.eventRepository = eventRepository;
        this.companyParticipationRepository = companyParticipationRepository;
        this.personParticipationRepository = personParticipationRepository;
    }

    /**
     * Validate if event fields are correct. Save event if correct.
     *
     * @param eventDto event to save.
     * @return ValidationResult with relevant error messages.
     */
    public ValidationResult createEvent(EventDto eventDto) {
        ValidationResult validationResult = eventValidator.validate(eventDto);

        if (!validationResult.isValid()) {
            return validationResult;
        }
        Event event = new Event(eventDto.getName(), eventDto.getTime(),
                eventDto.getLocation(), eventDto.getAdditionalInfo());
        try {
            eventRepository.save(event);
            LOGGER.info(String.format("Event successfully created: %1$s at %2$s",
                    eventDto.getName(), eventDto.getLocation()));
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            validationResult.addError("Failed to save event.");
        }
        return validationResult;
    }

    /**
     * Delete event by id, if it has not started yet.
     *
     * @param id ID of event.
     * @return true if event was deleted, else false.
     */
    public boolean deleteEventById(Long id) {
        Optional<Event> optionalEvent = eventRepository.findEventById(id);
        if (optionalEvent.isEmpty()) {
            return false;
        }
        Event event = optionalEvent.get();
        if (event.getTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        try {
            companyParticipationRepository.deleteAllByEvent(event);
            personParticipationRepository.deleteAllByEvent(event);
            eventRepository.deleteById(id);
            LOGGER.info(String.format("Deleted event: %1$s", event.getName()));
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return false;
        }

        return true;
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
            summary.setId(event.getId());
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
        List<CompanyParticipation> companies = companyParticipationRepository.getCompanyParticipationsByEvent_Id(eventId);
        List<PersonParticipation> persons = personParticipationRepository.getPersonParticipationsByEvent_Id(eventId);

        int result = 0;
        for (CompanyParticipation participation : companies) {
            result += participation.getNumberOfParticipants();
        }
        result += persons.size();
        return result;
    }

    /**
     * Create summary of all participants in an event.
     *
     * @param eventId ID of the event.
     * @return summaries as a list.
     */
    public List<ParticipantSummaryDto> getEventParticipantSummariesList(Long eventId) {
        List<CompanyParticipation> companies = companyParticipationRepository.getCompanyParticipationsByEvent_Id(eventId);
        List<PersonParticipation> persons = personParticipationRepository.getPersonParticipationsByEvent_Id(eventId);
        List<ParticipantSummaryDto> result = new ArrayList<>();

        for (CompanyParticipation participation : companies) {
            ParticipantSummaryDto dto = new ParticipantSummaryDto();
            dto.setParticipationId(participation.getId());
            dto.setName(participation.getCompanyName());
            dto.setIdCode(participation.getRegistryCode());
            result.add(dto);
        }

        for (PersonParticipation participation : persons) {
            ParticipantSummaryDto dto = new ParticipantSummaryDto();
            dto.setParticipationId(participation.getId());
            dto.setName(String.format("%1$s %2$s", participation.getFirstName(), participation.getLastName()));
            dto.setIdCode(participation.getPersonalCode());
            result.add(dto);
        }
        return result;
    }

    /**
     * Add new person participation if fields are valid.
     *
     * @param personDto participation info.
     * @return validation result.
     */
    public ValidationResult addPersonParticipation(PersonParticipationDto personDto) {
        ValidationResult validationResult = participationValidator.validatePerson(personDto);

        Event event = null;
        Optional<Event> optionalEvent = eventRepository.findEventById(personDto.getEventId());
        if (optionalEvent.isEmpty()) {
            String message = "Event not found.";
            validationResult.addError(message);
        } else {
            event = optionalEvent.get();
        }

        PaymentMethod paymentMethod = null;
        Optional<PaymentMethod> optionalPayment = PaymentMethod.fromDisplayName(personDto.getPaymentMethod());
        if (optionalPayment.isPresent()) {
            paymentMethod = optionalPayment.get();
        } else {
            validationResult.addError("Invalid type of payment.");
        }

        if (!validationResult.isValid()) {
            return validationResult;
        }

        PersonParticipation participation = new PersonParticipation(
                event,
                paymentMethod,
                personDto.getAdditionalInfo(),
                personDto.getFirstName(),
                personDto.getLastName(),
                personDto.getPersonalCode()
        );
        personParticipationRepository.save(participation);
        LOGGER.info(String.format("Added %1$s %2$s to event %3$s",
                participation.getFirstName(), participation.getLastName(), participation.getEvent().getName()));
        return validationResult;
    }

    /**
     * Add new company participation if valid.
     *
     * @param companyDto info of participation.
     * @return validation result.
     */
    public ValidationResult addCompanyParticipation(CompanyParticipationDto companyDto) {
        ValidationResult validationResult = participationValidator.validateCompany(companyDto);

        if (!validationResult.isValid()) {
            return validationResult;
        }

        Event event = null;
        Optional<Event> optionalEvent = eventRepository.findEventById(companyDto.getEventId());
        if (optionalEvent.isEmpty()) {
            String message = "Event not found.";
            validationResult.addError(message);
        } else {
            event = optionalEvent.get();
        }

        PaymentMethod paymentMethod = null;

        Optional<PaymentMethod> optionalPayment = PaymentMethod.fromDisplayName(companyDto.getPaymentMethod());
        if (optionalPayment.isPresent()) {
            paymentMethod = optionalPayment.get();
        } else {
            validationResult.addError("Invalid type of payment.");
        }

        CompanyParticipation participation = new CompanyParticipation(
                event,
                paymentMethod,
                companyDto.getAdditionalInfo(),
                companyDto.getCompanyName(),
                companyDto.getRegistrationCode(),
                companyDto.getNumberOfParticipants()
        );
        companyParticipationRepository.save(participation);
        LOGGER.info(String.format("Added %1$s to event %2$s",
                participation.getCompanyName(), participation.getEvent().getName()));
        return validationResult;
    }

}

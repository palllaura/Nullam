package com.rik.nullam.service;

import com.rik.nullam.dto.CompanyParticipationDto;
import com.rik.nullam.dto.EventDto;
import com.rik.nullam.dto.EventSummaryDto;
import com.rik.nullam.dto.ParticipationSummaryDto;
import com.rik.nullam.dto.PersonParticipationDto;
import com.rik.nullam.dto.ValidationResult;
import com.rik.nullam.entity.event.Event;

import com.rik.nullam.entity.participation.CompanyParticipation;
import com.rik.nullam.entity.participation.PaymentMethod;
import com.rik.nullam.entity.participation.PersonParticipation;
import com.rik.nullam.repository.CompanyParticipationRepository;
import com.rik.nullam.repository.EventRepository;
import com.rik.nullam.repository.PersonParticipationRepository;
import jakarta.transaction.Transactional;
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
    @Transactional
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
     * Get event summary by event id;
     * @param eventId id of event.
     * @return event summary.
     */
    public EventSummaryDto getEventSummaryById(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findEventById(eventId);
        if (optionalEvent.isEmpty()) {
            return null;
        }
        Event event = optionalEvent.get();
        EventSummaryDto dto = new EventSummaryDto();
        dto.setId(eventId);
        dto.setName(event.getName());
        dto.setTime(event.getTime());
        dto.setLocation(event.getLocation());
        dto.setNumberOfParticipants(calculateNumberOfParticipants(eventId));
        return dto;
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
    public List<ParticipationSummaryDto> getEventParticipantSummariesList(Long eventId) {
        List<CompanyParticipation> companies = companyParticipationRepository.getCompanyParticipationsByEvent_Id(eventId);
        List<PersonParticipation> persons = personParticipationRepository.getPersonParticipationsByEvent_Id(eventId);
        List<ParticipationSummaryDto> result = new ArrayList<>();

        for (CompanyParticipation participation : companies) {
            ParticipationSummaryDto dto = new ParticipationSummaryDto();
            dto.setParticipationId(participation.getId());
            dto.setName(participation.getCompanyName());
            dto.setIdCode(participation.getRegistryCode());
            dto.setType(ParticipationSummaryDto.ParticipationType.COMPANY);
            result.add(dto);
        }

        for (PersonParticipation participation : persons) {
            ParticipationSummaryDto dto = new ParticipationSummaryDto();
            dto.setParticipationId(participation.getId());
            dto.setName(String.format("%1$s %2$s", participation.getFirstName(), participation.getLastName()));
            dto.setIdCode(participation.getPersonalCode());
            dto.setType(ParticipationSummaryDto.ParticipationType.PERSON);
            result.add(dto);
        }
        return result;
    }

    /**
     * Get person participation info.
     * @param participationId id of participation.
     * @return personParticipationDto
     */
    public PersonParticipationDto getPersonParticipationInfo(Long participationId) {
        Optional<PersonParticipation> optional = personParticipationRepository.findById(participationId);
        if (optional.isEmpty()) {
            return null;
        }
        PersonParticipation participation = optional.get();
        PersonParticipationDto dto = new PersonParticipationDto();

        dto.setParticipationId(participationId);
        dto.setEventId(participation.getEvent().getId());
        dto.setFirstName(participation.getFirstName());
        dto.setLastName(participation.getLastName());
        dto.setPaymentMethod(String.valueOf(participation.getPaymentMethod()));
        dto.setPersonalCode(participation.getPersonalCode());
        dto.setAdditionalInfo(participation.getAdditionalInfo());
        return dto;
    }

    /**
     * Get person participation info.
     * @param participationId id of participation.
     * @return personParticipationDto
     */
    public CompanyParticipationDto getCompanyParticipationInfo(Long participationId) {
        Optional<CompanyParticipation> optional = companyParticipationRepository.findById(participationId);
        if (optional.isEmpty()) {
            return null;
        }
        CompanyParticipation participation = optional.get();
        CompanyParticipationDto dto = new CompanyParticipationDto();

        dto.setParticipationId(participationId);
        dto.setEventId(participation.getEvent().getId());
        dto.setCompanyName(participation.getCompanyName());
        dto.setPaymentMethod(String.valueOf(participation.getPaymentMethod()));
        dto.setRegistrationCode(participation.getRegistryCode());
        dto.setNumberOfParticipants(participation.getNumberOfParticipants());
        dto.setAdditionalInfo(participation.getAdditionalInfo());
        return dto;
    }

    /**
     * Add new person participation if fields are valid.
     *
     * @param personDto participation info.
     * @return validation result.
     */
    public ValidationResult addPersonParticipation(PersonParticipationDto personDto) {
        ValidationResult validationResult = participationValidator.validatePerson(personDto);
        if (!validationResult.isValid()) return validationResult;

        Optional<Event> optionalEvent = eventRepository.findEventById(personDto.getEventId());
        PaymentMethod payment = PaymentMethod.valueOf(personDto.getPaymentMethod());

        PersonParticipation participation = new PersonParticipation(
                optionalEvent.get(),
                payment,
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

        if (!validationResult.isValid()) return validationResult;

        Optional<Event> optionalEvent = eventRepository.findEventById(companyDto.getEventId());
        PaymentMethod payment = PaymentMethod.valueOf(companyDto.getPaymentMethod());

        CompanyParticipation participation = new CompanyParticipation(
                optionalEvent.get(),
                payment,
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

    /**
     * Edit person participation.
     * @param dto person info.
     * @return validation result.
     */
    public ValidationResult editPersonParticipation(PersonParticipationDto dto) {
        Optional<PersonParticipation> optional = personParticipationRepository.findById(dto.getParticipationId());
        if (optional.isEmpty()) {
            ValidationResult result = new ValidationResult();
            result.addError("Participation not found.");
            return result;
        }
        PersonParticipation participation = optional.get();
        dto.setEventId(participation.getEvent().getId());

        ValidationResult validationResult = participationValidator.validatePerson(dto);
        if (!validationResult.isValid()) return validationResult;

        participation.setFirstName(dto.getFirstName());
        participation.setLastName(dto.getLastName());
        participation.setPersonalCode(dto.getPersonalCode());
        participation.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
        participation.setAdditionalInfo(dto.getAdditionalInfo());

        personParticipationRepository.save(participation);

        return validationResult;
    }

    /**
     * Edit company participation.
     * @param dto company info.
     * @return validation result.
     */
    public ValidationResult editCompanyParticipation(CompanyParticipationDto dto) {
        Optional<CompanyParticipation> optional = companyParticipationRepository.findById(dto.getParticipationId());
        if (optional.isEmpty()) {
            ValidationResult result = new ValidationResult();
            result.addError("Participation not found.");
            return result;
        }
        CompanyParticipation participation = optional.get();
        dto.setEventId(participation.getEvent().getId());

        ValidationResult validationResult = participationValidator.validateCompany(dto);
        if (!validationResult.isValid()) return validationResult;

        participation.setCompanyName(dto.getCompanyName());
        participation.setRegistryCode(dto.getRegistrationCode());
        participation.setNumberOfParticipants(dto.getNumberOfParticipants());
        participation.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
        participation.setAdditionalInfo(dto.getAdditionalInfo());

        companyParticipationRepository.save(participation);

        return validationResult;
    }

    /**
     * Delete participation if event is not in the past.
     * @param type type of participation - Person or Company
     * @param participationId id of participation.
     * @return true if was deleted, else false.
     */
    public boolean deleteParticipation(ParticipationSummaryDto.ParticipationType type, Long participationId) {
        if (type.equals(ParticipationSummaryDto.ParticipationType.PERSON)) {
            Optional<PersonParticipation> optional = personParticipationRepository.findById(participationId);
            if (optional.isPresent()) {
                PersonParticipation participation = optional.get();
                if (!checkIfEventIsInThePast(participation.getEvent())) {
                    personParticipationRepository.deleteById(participationId);
                    return true;
                }
            }
        } else if (type.equals(ParticipationSummaryDto.ParticipationType.COMPANY)) {
            Optional<CompanyParticipation> optional = companyParticipationRepository.findById(participationId);
            if (optional.isPresent()) {
                CompanyParticipation participation = optional.get();
                if (!checkIfEventIsInThePast(participation.getEvent())) {
                    companyParticipationRepository.deleteById(participationId);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if event has already happened.
     * @param event Event to check.
     * @return true if event is in the past, else false.
     */
    private boolean checkIfEventIsInThePast(Event event) {
        return event.getTime().isBefore(LocalDateTime.now());
    }
}

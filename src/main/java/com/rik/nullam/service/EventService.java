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
    @Transactional
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
        return new EventSummaryDto(eventId, event.getName(), event.getTime(), event.getLocation(),
                calculateNumberOfParticipants(eventId));
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
            result.add(new EventSummaryDto(event.getId(), event.getName(), event.getTime(), event.getLocation(),
                    calculateNumberOfParticipants(event.getId())));
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
            result.add(new ParticipationSummaryDto(participation.getCompanyName(), participation.getRegistryCode(),
                    participation.getId(), ParticipationSummaryDto.ParticipationType.COMPANY));
        }

        for (PersonParticipation participation : persons) {
            result.add(new ParticipationSummaryDto(
                    String.format("%1$s %2$s", participation.getFirstName(), participation.getLastName()),
                    participation.getPersonalCode(), participation.getId(),
                    ParticipationSummaryDto.ParticipationType.PERSON));
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
        return new PersonParticipationDto(
                participationId, participation.getEvent().getId(), String.valueOf(participation.getPaymentMethod()),
                participation.getAdditionalInfo(), participation.getFirstName(), participation.getLastName(),
                participation.getPersonalCode()
        );

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
        return new CompanyParticipationDto(
                participationId, participation.getEvent().getId(), String.valueOf(participation.getPaymentMethod()),
                participation.getAdditionalInfo(), participation.getCompanyName(), participation.getRegistryCode(),
                participation.getNumberOfParticipants()
        );
    }

    /**
     * Add new person participation if fields are valid.
     *
     * @param personDto participation info.
     * @return validation result.
     */
    @Transactional
    public ValidationResult addPersonParticipation(PersonParticipationDto personDto) {
        ValidationResult validationResult = participationValidator.validatePerson(personDto);
        if (!validationResult.isValid()) return validationResult;

        Optional<Event> optionalEvent = eventRepository.findEventById(personDto.getEventId());
        PaymentMethod payment = PaymentMethod.valueOf(personDto.getPaymentMethod());

        PersonParticipation participation = new PersonParticipation(optionalEvent.get(), payment,
                personDto.getAdditionalInfo(), personDto.getFirstName(), personDto.getLastName(),
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
    @Transactional
    public ValidationResult addCompanyParticipation(CompanyParticipationDto companyDto) {
        ValidationResult validationResult = participationValidator.validateCompany(companyDto);

        if (!validationResult.isValid()) return validationResult;

        Optional<Event> optionalEvent = eventRepository.findEventById(companyDto.getEventId());
        PaymentMethod payment = PaymentMethod.valueOf(companyDto.getPaymentMethod());

        CompanyParticipation participation = new CompanyParticipation(optionalEvent.get(), payment,
                companyDto.getAdditionalInfo(), companyDto.getCompanyName(), companyDto.getRegistryCode(),
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
    @Transactional
    public ValidationResult editPersonParticipation(PersonParticipationDto dto) {
        Optional<PersonParticipation> optional = personParticipationRepository.findById(dto.getParticipationId());
        if (optional.isEmpty()) {
            ValidationResult result = new ValidationResult();
            result.addError("Participation not found.");
            return result;
        }
        ValidationResult validationResult = participationValidator.validatePerson(dto);
        if (!validationResult.isValid()) return validationResult;

        PersonParticipation participation = optional.get();

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
    @Transactional
    public ValidationResult editCompanyParticipation(CompanyParticipationDto dto) {
        Optional<CompanyParticipation> optional = companyParticipationRepository.findById(dto.getParticipationId());
        if (optional.isEmpty()) {
            ValidationResult result = new ValidationResult();
            result.addError("Participation not found.");
            return result;
        }

        ValidationResult validationResult = participationValidator.validateCompany(dto);
        if (!validationResult.isValid()) return validationResult;

        CompanyParticipation participation = optional.get();
        participation.setCompanyName(dto.getCompanyName());
        participation.setRegistryCode(dto.getRegistryCode());
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
    @Transactional
    public boolean deleteParticipation(ParticipationSummaryDto.ParticipationType type, Long participationId) {
        if (type.equals(ParticipationSummaryDto.ParticipationType.PERSON)) {
            Optional<PersonParticipation> optional = personParticipationRepository.findById(participationId);
            if (optional.isPresent() && !checkIfEventIsInThePast(optional.get().getEvent())) {
                personParticipationRepository.deleteById(participationId);
                return true;
            }
        } else if (type.equals(ParticipationSummaryDto.ParticipationType.COMPANY)) {
            Optional<CompanyParticipation> optional = companyParticipationRepository.findById(participationId);
            if (optional.isPresent() && !checkIfEventIsInThePast(optional.get().getEvent())) {
                companyParticipationRepository.deleteById(participationId);
                return true;
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

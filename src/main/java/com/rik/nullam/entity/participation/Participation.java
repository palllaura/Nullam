package com.rik.nullam.entity.participation;

import com.rik.nullam.entity.event.Event;
import com.rik.nullam.entity.participant.Participant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Participation class that links an event and a participant.
 */
@Getter
@Setter
@Entity
public class Participation {
    /**
     * Unique identifier for the participation.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Event the participant is attending.
     */
    @ManyToOne(optional = false)
    private Event event;

    /**
     * Participant attending the event.
     */
    @ManyToOne(optional = false)
    private Participant participant;

    /**
     * Number of attendees if it is a company, otherwise 1.
     */
    @Min(1)
    private int numberOfAttendees;

    /**
     * Payment method used by the participant (e.g., cash or bank transfer).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * Additional info or participant-specific requests.
     */
    @Size(max = 5000)
    @Column(length = 5000)
    private String additionalInfo;

    /**
     * Constructor for participation.
     *
     * @param event             Event the participant is attending.
     * @param participant       Participant attending the event.
     * @param numberOfAttendees Number of attendees (1 if not a company).
     * @param paymentMethod     Method of payment.
     * @param additionalInfo    Additional info.
     */
    public Participation(Event event, Participant participant, int numberOfAttendees,
                         PaymentMethod paymentMethod, String additionalInfo) {
        this.event = event;
        this.participant = participant;
        this.numberOfAttendees = numberOfAttendees;
        this.paymentMethod = paymentMethod;
        this.additionalInfo = additionalInfo;
    }

    /**
     * No-args constructor for JPA.
     */
    protected Participation() {
    }
}

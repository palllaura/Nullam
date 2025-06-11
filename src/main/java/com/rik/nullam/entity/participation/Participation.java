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
import jakarta.validation.constraints.Size;

/**
 * Participation class that links an event and a participant.
 */
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
     * @param event Event the participant is attending.
     * @param participant Participant attending the event.
     * @param paymentMethod Method of payment.
     * @param additionalInfo Additional info.
     */
    public Participation(Event event, Participant participant, PaymentMethod paymentMethod, String additionalInfo) {
        this.event = event;
        this.participant = participant;
        this.paymentMethod = paymentMethod;
        this.additionalInfo = additionalInfo;
    }

    /**
     * No-args constructor for JPA.
     */
    public Participation() {
    }
}

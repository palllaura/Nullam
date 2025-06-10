package com.rik.nullam.entity.participant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * Abstract base class for all participants (persons and companies).
 * Stores common information like payment method and additional info.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Participant {

    /**
     * Unique identifier for the participant.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Payment method used by the participant (e.g., cash or bank transfer).
     */
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /**
     * Additional info or participant-specific requests.
     */
    private String additionalInfo;
}



package com.rik.nullam.entity.participation;

import com.rik.nullam.entity.event.Event;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Participation {
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
     * No-args constructor for JPA.
     */
    protected Participation() {
    }

    /**
     * Participation constructor.
     * @param event Event.
     * @param paymentMethod Method of payment.
     * @param additionalInfo Addtitional info.
     */
    protected Participation(Event event, PaymentMethod paymentMethod, String additionalInfo) {
        this.event = event;
        this.paymentMethod = paymentMethod;
        this.additionalInfo = additionalInfo;
    }
}

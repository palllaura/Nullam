package com.rik.nullam.entity.participation;

import com.rik.nullam.entity.event.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PersonParticipation extends Participation {
    /**
     * First name of participant.
     */
    @Column(nullable = false)
    private String firstName;
    /**
     * Last name of participant.
     */
    @Column(nullable = false)
    private String lastName;
    /**
     * Personal code of participant.
     */
    @Column(nullable = false)
    private String personalCode;

    /**
     * No-args constructor for JPA.
     */
    protected PersonParticipation() {
    }

    /**
     * Person participation class.
     * @param event Event.
     * @param paymentMethod Method of payment.
     * @param additionalInfo Additional info.
     * @param firstName First name of person.
     * @param lastName Last name of person.
     * @param personalCode Personal code.
     */
    public PersonParticipation(Event event, PaymentMethod paymentMethod, String additionalInfo,
                               String firstName, String lastName, String personalCode) {
        super(event, paymentMethod, additionalInfo);
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalCode = personalCode;
    }
}

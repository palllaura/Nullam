package com.rik.nullam.entity.participation;

import com.rik.nullam.entity.event.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CompanyParticipation extends Participation {
    /**
     * Name of company.
     */
    @Column(nullable = false)
    private String companyName;
    /**
     * Registry code of company.
     */
    @Column(nullable = false)
    private String registryCode;
    /**
     * Number of participants attending from company.
     */
    @Column(nullable = false)
    private int numberOfParticipants;

    /**
     * No-args constructor for JPA.
     */
    protected CompanyParticipation() {
    }

    /**
     * Company participation constructor.
     * @param event Event.
     * @param paymentMethod Method of payment.
     * @param additionalInfo Additional info.
     * @param companyName Name of company.
     * @param registryCode Registration code of company.
     * @param numberOfParticipants Number of people attending event.
     */
    public CompanyParticipation(Event event, PaymentMethod paymentMethod, String additionalInfo,
                                String companyName, String registryCode, int numberOfParticipants) {
        super(event, paymentMethod, additionalInfo);
        this.companyName = companyName;
        this.registryCode = registryCode;
        this.numberOfParticipants = numberOfParticipants;
    }
}

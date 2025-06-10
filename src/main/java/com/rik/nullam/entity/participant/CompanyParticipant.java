package com.rik.nullam.entity.participant;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Company participants class that stores information unique to companies as participants.
 */
@Entity
@AttributeOverride(name = "additionalInfo", column = @Column(length = 5000))
public class CompanyParticipant extends Participant {
    /**
     * Name of company.
     */
    private String companyName;
    /**
     * Registry code of company, must be unique.
     */
    @Column(unique = true)
    private String registryCode;
    /**
     * Number of attendees from company.
     */
    private int numberOfAttendees;

}


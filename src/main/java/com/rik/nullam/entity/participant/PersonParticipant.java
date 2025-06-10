package com.rik.nullam.entity.participant;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Person participants class that stores information unique to individual person participants.
 */
@Entity
@AttributeOverride(name = "additionalInfo", column = @Column(length = 1500))
public class PersonParticipant extends Participant {
    /**
     * First name of person.
     */
    private String firstName;
    /**
     * Last name of person.
     */
    private String lastName;
    /**
     * Personal code of person, must be unique.
     */
    @Column(unique = true)
    private String personalCode;

}


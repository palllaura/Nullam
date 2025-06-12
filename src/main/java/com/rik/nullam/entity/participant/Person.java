package com.rik.nullam.entity.participant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Person class that stores information about individual person participants.
 */
@Getter
@Setter
@Entity
public class Person extends Participant {
    /**
     * First name of person.
     */
    @Column(nullable = false)
    private String firstName;
    /**
     * Last name of person.
     */
    @Column(nullable = false)
    private String lastName;
    /**
     * Personal code of person, must be unique.
     */
    @Column(unique = true, nullable = false)
    private String personalCode;

    /**
     * Constructor for Person.
     *
     * @param firstName    First name of person.
     * @param lastName     Last name of person.
     * @param personalCode Unique personal code.
     */
    public Person(String firstName, String lastName, String personalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalCode = personalCode;
    }

    /**
     * No-args constructor for JPA.
     */
    protected Person() {

    }
}


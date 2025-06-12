package com.rik.nullam.entity.participant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;

/**
 * Abstract base class for all participants (persons and companies).
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)@Getter
public abstract class Participant {

    /**
     * Unique identifier for the participant.
     */
    @Id
    @GeneratedValue
    private Long id;

}

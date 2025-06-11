package com.rik.nullam.entity.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Event class that stores information like event time and location.
 */
@Entity
@Getter
public class Event {
    /**
     * Unique identifier for the event.
     */
    @Id
    @GeneratedValue
    private Long id;
    /**
     * Starting time of the event.
     */
    @Column(nullable = false)
    private LocalDateTime time;
    /**
     * Location of the event.
     */
    @Column(nullable = false)
    private String location;
    /**
     * Additional information about the event.
     */
    @Size(max = 1000)
    @Column(length = 1000)
    private String additionalInfo;

    /**
     * Constructor for events.
     *
     * @param time           time of event.
     * @param location       location of event.
     * @param additionalInfo additional info.
     */
    public Event(LocalDateTime time, String location, String additionalInfo) {
        this.time = time;
        this.location = location;
        this.additionalInfo = additionalInfo;
    }

    /**
     * No-args constructor for JPA.
     */
    protected Event() {
    }
}

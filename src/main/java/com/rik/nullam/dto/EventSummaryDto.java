package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventSummaryDto {
    /**
     * ID of the event.
     */
    private Long id;
    /**
     * Name fo the event.
     */
    private String name;
    /**
     * Starting time of the event.
     */
    private LocalDateTime time;
    /**
     * Location of the event.
     */
    private String location;
    /**
     * Number of participants.
     */
    private int numberOfParticipants;

    /**
     * Constructor for event summary dto.
     * @param id id of event.
     * @param name name of event.
     * @param time time of event.
     * @param location location of event.
     * @param numberOfParticipants number of participants.
     */
    public EventSummaryDto(Long id, String name, LocalDateTime time, String location, int numberOfParticipants) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.location = location;
        this.numberOfParticipants = numberOfParticipants;
    }

    /**
     * No-args constructor.
     */
    public EventSummaryDto() {
    }
}

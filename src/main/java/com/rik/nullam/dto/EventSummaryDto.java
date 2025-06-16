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

}

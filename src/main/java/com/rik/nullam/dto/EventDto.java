package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDto {
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
     * Additional information about the event.
     */
    private String additionalInfo;

}

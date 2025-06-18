package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonParticipationDto {

    /**
     * ID of the event to which person will participate.
     */
    private Long eventId;

    /**
     * Payment method for participation.
     */
    private String paymentMethod;

    /**
     * Additional information.
     */
    private String additionalInfo;

    /**
     * First name of the person.
     */
    private String firstName;

    /**
     * Last name of the person.
     */
    private String lastName;

    /**
     * Personal code of the person.
     */
    private String personalCode;
}

package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonParticipationDto {
    /**
     * Id od participation.
     */
    private Long participationId;

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

    /**
     * Constructor for person participation dto.
     * @param participationId participation id.
     * @param eventId event id.
     * @param paymentMethod payment method.
     * @param additionalInfo additional info.
     * @param firstName fist name.
     * @param lastName last name.
     * @param personalCode personal code.
     */
    public PersonParticipationDto(Long participationId, Long eventId, String paymentMethod, String additionalInfo,
                                  String firstName, String lastName, String personalCode) {
        this.participationId = participationId;
        this.eventId = eventId;
        this.paymentMethod = paymentMethod;
        this.additionalInfo = additionalInfo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalCode = personalCode;
    }

    /**
     * No-args constructor.
     */
    public PersonParticipationDto() {
    }
}

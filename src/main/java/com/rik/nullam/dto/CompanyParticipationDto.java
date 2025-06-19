package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyParticipationDto {
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
     * Name of the company.
     */
    private String companyName;

    /**
     * Registration code of the company.
     */
    private String registrationCode;
    /**
     * Number on participants from company.
     */
    private Integer numberOfParticipants;
}

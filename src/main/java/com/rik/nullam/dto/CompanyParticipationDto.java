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
    private String registryCode;
    /**
     * Number on participants from company.
     */
    private Integer numberOfParticipants;

    /**
     * CompanyParticipationDto constructor with fields.
     * @param participationId participation id.
     * @param eventId event id.
     * @param paymentMethod payment method.
     * @param additionalInfo additional info.
     * @param companyName company name.
     * @param registryCode registration code.
     * @param numberOfParticipants number of participants.
     */
    public CompanyParticipationDto(Long participationId, Long eventId, String paymentMethod, String additionalInfo,
                                   String companyName, String registryCode, Integer numberOfParticipants) {
        this.participationId = participationId;
        this.eventId = eventId;
        this.paymentMethod = paymentMethod;
        this.additionalInfo = additionalInfo;
        this.companyName = companyName;
        this.registryCode = registryCode;
        this.numberOfParticipants = numberOfParticipants;
    }

    /**
     * No-args constructor.
     */
    public CompanyParticipationDto() {
    }
}

package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipationSummaryDto {
    /**
     * Full name (for person) or legal name (for company).
     */
    private String name;

    /**
     * Personal ID (for person) or registry code (for company).
     */
    private String idCode;

    /**
     * ID of the participation record.
     */
    private Long participationId;

    /**
     * Type of participation.
     */
    private ParticipationType type;

    /**
     * Type of participation.
     */
    public enum ParticipationType {
        PERSON,
        COMPANY
    }
}

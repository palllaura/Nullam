package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDto {
    /**
     * First name of person.
     */
    private String firstName;
    /**
     * Last name of person.
     */
    private String lastName;
    /**
     * Personal code of person, must be unique.
     */
    private String personalCode;

}

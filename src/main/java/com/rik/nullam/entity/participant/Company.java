package com.rik.nullam.entity.participant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Company class that stores information about companies as participants.
 */
@Getter
@Setter
@Entity
public class Company extends Participant {
    /**
     * Name of company.
     */
    @Column(nullable = false)
    private String companyName;
    /**
     * Registry code of company, must be unique.
     */
    @Column(unique = true, nullable = false)
    private String registryCode;

    /**
     * Company constructor.
     * @param companyName Name of company.
     * @param registryCode Unique registry code.
     */
    public Company(String companyName, String registryCode) {
        this.companyName = companyName;
        this.registryCode = registryCode;
    }

    /**
     * No-args constructor for JPA.
     */
    protected Company() {
    }


}


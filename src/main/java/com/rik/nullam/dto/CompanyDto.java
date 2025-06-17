package com.rik.nullam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    /**
     * Name of company
     */
    private String companyName;
    /**
     * Registry code of company, must be unique.
     */
    private String registryCode;

}

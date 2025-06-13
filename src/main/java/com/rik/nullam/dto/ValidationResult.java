package com.rik.nullam.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the result of validation and possible error messages in case of failure.
 */
@Getter
public class ValidationResult {

    private boolean valid;
    private final List<String> messages = new ArrayList<>();

    /**
     * ValidationResult constructor.
     */
    public ValidationResult() {
        this.valid = true;
    }

    /**
     * Add error message and change valid to false.
     * @param message error message to add.
     */
    public void addError(String message) {
        this.valid = false;
        this.messages.add(message);
    }

}


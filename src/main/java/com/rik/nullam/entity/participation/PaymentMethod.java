package com.rik.nullam.entity.participation;

import lombok.Getter;

/**
 * Enum class that holds different payment options.
 */
@Getter
public enum PaymentMethod {
    BANK_TRANSFER("Bank transfer"),
    CASH("Cash");
    /**
     * Display name of payment method.
     */
    private final String displayName;

    /**
     * Constructor for a payment method with display name.
     * @param displayName display name as string.
     */
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

}

package com.rik.nullam.entity.participation;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

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

    /**
     * Find matching payment type from display name.
     * @param displayName display name to find.
     * @return optional of found type.
     */
    public static Optional<PaymentMethod> fromDisplayName(String displayName) {
        return Arrays.stream(PaymentMethod.values())
                .filter(pm -> pm.getDisplayName().equals(displayName))
                .findFirst();
    }

}

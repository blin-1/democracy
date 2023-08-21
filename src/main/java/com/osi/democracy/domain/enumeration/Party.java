package com.osi.democracy.domain.enumeration;

/**
 * The Party enumeration.
 */
public enum Party {
    R("Republican"),
    D("Democrat"),
    I("Independent"),
    U("Unaffiliated");

    private final String value;

    Party(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

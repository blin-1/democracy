package com.osi.democracy.domain.enumeration;

/**
 * The YesNo enumeration.
 */
public enum YesNo {
    Y("Yes"),
    N("No");

    private final String value;

    YesNo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

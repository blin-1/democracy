package com.osi.democracy.domain.enumeration;

/**
 * The State enumeration.
 */
public enum State {
    AL("Alabama"),
    NJ("New Jersey"),
    NY("New York");

    private final String value;

    State(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

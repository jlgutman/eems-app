package com.eems.model;

public enum ProjectStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    ON_HOLD("On Hold"),
    CANCELLED("Cancelled");

    private final String dbValue;

    ProjectStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() { return dbValue; }

    public static ProjectStatus fromDbValue(String value) {
        for (ProjectStatus s : values()) {
            if (s.dbValue.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown project status: '" + value + "'");
    }

    @Override
    public String toString() { return dbValue; }
}

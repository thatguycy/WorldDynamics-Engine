package com.thatguycy.worlddynamicsengine;

import java.io.Serializable;

public class NationProperties {
    private GovernmentType governmentType;
    // Add more properties here as needed, like PoliticalSystem, EconomyType, etc.

    public NationProperties(GovernmentType governmentType) {
        this.governmentType = governmentType;
        // Initialize other properties here
    }

    public GovernmentType getGovernmentType() {
        return governmentType;
    }

    public void setGovernmentType(GovernmentType governmentType) {
        this.governmentType = governmentType;
    }

    // Add getters and setters for other properties
}

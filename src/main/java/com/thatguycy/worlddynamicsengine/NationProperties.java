package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.object.Resident;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class NationProperties implements Serializable {
    private String governmentType;
    private Resident armyLeader;
    private Set<String> armyMembers; // Storing army members by their names
    private Resident governmentLeader;
    private Set<String> governmentMembers;

    public NationProperties(String governmentType) {
        this.governmentType = governmentType;
        this.armyMembers = new HashSet<>(); // Initialize the set
        this.governmentMembers = new HashSet<>();
    }


    public String getGovernmentType() {
        return governmentType;
    }

    public void setGovernmentType(String governmentType) {
        this.governmentType = governmentType;
    }

    public Resident getGovernmentLeader() {
        return governmentLeader;
    }

    public void setGovernmentLeader(Resident armyLeader) {
        this.governmentLeader = armyLeader;
    }

    public void addGovernmentMember(String memberName) {
        governmentMembers.add(memberName);
    }

    public void removeGovernmentMember(String memberName) {
        governmentMembers.remove(memberName);
    }

    public Set<String> getGovernmentMembers() {
        return new HashSet<>(governmentMembers); // Return a copy to prevent external modification
    }

    public Resident getArmyLeader() {
        return armyLeader;
    }

    public void setArmyLeader(Resident armyLeader) {
        this.armyLeader = armyLeader;
    }

    public void addArmyMember(String memberName) {
        armyMembers.add(memberName);
    }

    public void removeArmyMember(String memberName) {
        armyMembers.remove(memberName);
    }

    public Set<String> getArmyMembers() {
        return new HashSet<>(armyMembers); // Return a copy to prevent external modification
    }

    // Additional getters and setters for other properties
}

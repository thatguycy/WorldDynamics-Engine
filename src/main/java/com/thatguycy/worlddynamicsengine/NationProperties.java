package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.object.Resident;
import java.io.Serializable;
import java.util.*;

public class NationProperties implements Serializable {
    private String governmentType;
    private Resident armyLeader;
    private Set<String> armyMembers; // Storing army members by their names
    private Resident governmentLeader;
    private Set<String> governmentMembers;

    private Map<Integer, String> laws; // Storing laws with an assigned ID

    public NationProperties(String governmentType) {
        this.governmentType = governmentType;
        this.armyMembers = new HashSet<>(); // Initialize the set
        this.governmentMembers = new HashSet<>();
        this.laws = new HashMap<>();
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

    public void addLaw(String law) {
        int nextId = laws.isEmpty() ? 1 : Collections.max(laws.keySet()) + 1;
        laws.put(nextId, law);
    }

    public void removeLaw(int id) {
        System.out.println("Attempting to remove law with ID: " + id);
        if (laws.containsKey(id)) {
            laws.remove(id);
            System.out.println("Law removed successfully.");
        } else {
            System.out.println("No law found with ID: " + id);
        }
    }

    public String getLaw(int id) {
        return laws.get(id);
    }

    public Map<Integer, String> getLaws() {
        return new HashMap<>(laws); // Return a copy to prevent external modification
    }

    // Additional getters and setters for other properties
}

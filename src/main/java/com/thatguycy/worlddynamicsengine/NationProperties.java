package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.object.Resident;

import java.util.*;

public class NationProperties {
    private String nationName;
    private UUID nationUUID;
    private String governmentType;
    private Resident governmentLeader;
    private List<Resident> governmentMembers = new ArrayList<>();
    private Resident armyLeader;
    private List<Resident> armyMembers = new ArrayList<>();

    // New in v0.2.x (SPICY!!!!!)
    private String culturalInfluence;
    private List<String> tradeAgreements = new ArrayList<>();
    private String nationalLanguage;
    private Map<String, Integer> resourceReserves = new HashMap<>();
    private List<String> nationalHolidays = new ArrayList<>();
    private String economicSystem;
    private Map<String, String> espionageNetwork = new HashMap<>();
    private List<String> technologicalAdvancements = new ArrayList<>();
    private String publicHealthSystem;
    private String educationSystem;
    private Map<String, String> immigrationPolicies = new HashMap<>();
    private Map<String, String> environmentalPolicies = new HashMap<>();
    private String nationalAnthem;
    private List<String> historicalEvents = new ArrayList<>();
    private String nationalSymbols;
    private String legislativeBody;
    private String judicialSystem;
    private Map<String, String> infrastructureDevelopment = new HashMap<>();
    private Map<String, Double> nationalTreasury = new HashMap<>();
    private String internationalInfluence;
    private Map<Integer, String> laws = new HashMap<>();

    // Constructors
    public NationProperties(String nationName, UUID nationUUID) {
        this.nationName = nationName;
        this.nationUUID = nationUUID;
    }

    // Getters and setters
    public String getNationName() {
        return nationName;
    }

    public void setNationName(String nationName) {
        this.nationName = nationName;
    }

    public UUID getNationUUID() {
        return nationUUID;
    }

    public void setNationUUID(UUID nationUUID) {
        this.nationUUID = nationUUID;
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

    public void setGovernmentLeader(Resident governmentLeader) {
        this.governmentLeader = governmentLeader;
    }

    public List<Resident> getGovernmentMembers() {
        return new ArrayList<>(governmentMembers);
    }

    public void addGovernmentMember(Resident member) {
        this.governmentMembers.add(member);
    }

    public void removeGovernmentMember(Resident member) {
        this.governmentMembers.remove(member);
    }

    public Resident getArmyLeader() {
        return armyLeader;
    }

    public void setArmyLeader(Resident armyLeader) {
        this.armyLeader = armyLeader;
    }

    public List<Resident> getArmyMembers() {
        return new ArrayList<>(armyMembers);
    }

    public void addArmyMember(Resident member) {
        this.armyMembers.add(member);
    }

    public void removeArmyMember(Resident member) {
        this.armyMembers.remove(member);
    }

    public Map<Integer, String> getLaws() {
        return new HashMap<>(laws);
    }

    public void setLaw(Integer number, String description) {
        this.laws.put(number, description);
    }

    public void removeLaw(Integer number) {
        this.laws.remove(number);
    }
}

package com.thatguycy.worlddynamicsengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

@SerializableAs("WDEnation")
public class WDEnation implements ConfigurationSerializable {
    private String nationName;

    private String governmentType;
    private String governmentLeader;
    private List<String> governmentMembers;
    private String armyCommander;
    private List<String> armyMembers;
    private List<String> tradeAgreements;
    private String diplomaticStatus;
    private String resourceControl;
    private String economicPolicies;
    private String culturalInfluence;
    private String militaryStrength;
    private String technologicalLevel;
    private String environmentalPolicies;
    private List<String> espionageNetwork;
    private List<String> historicalEvents;

    public WDEnation(String nationName) {
        this.nationName = nationName;
        this.governmentType = "None"; // Default value
        this.governmentLeader = "None"; // Default value
        this.governmentMembers = new ArrayList<>(); // Empty list
        this.armyCommander = "None";
        this.armyMembers = new ArrayList<>();
    }

    public WDEnation(Map<String, Object> map) {
        this.nationName = (String) map.get("nationName"); // Make sure to store the nation name in the map
        this.governmentType = (String) map.get("GovernmentType");
        this.governmentLeader = (String) map.get("GovernmentLeader");
        this.governmentMembers = (List<String>) map.get("GovernmentMembers");
        this.armyCommander = (String) map.get("ArmyCommander");
        this.armyMembers = (List<String>) map.get("ArmyMembers");
        // Initialize other properties from the map...
    }
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("GovernmentType", governmentType);
        map.put("GovernmentLeader", governmentLeader);
        map.put("GovernmentMembers", governmentMembers);
        map.put("ArmyCommander", armyCommander);
        map.put("ArmyMembers", armyMembers);
        return map;
    }

    public static WDEnation deserialize(Map<String, Object> map) {
        return new WDEnation(map);
    }

    public String getGovernmentType() {
        return this.governmentType;
    }

    public String getGovernmentLeader() {
        return this.governmentLeader;
    }

    public List<String> getGovernmentMembers() {
        return this.governmentMembers;
    }

    public String getArmyLeader() {
        return this.armyCommander;
    }

    public List<String> getArmyMembers() {
        return this.armyMembers;
    }

    public String getNationName() {
        return this.nationName;
    }
    public void setGovernmentType(String govType) {
        this.governmentType = govType;
    }

    public void setGovernmentLeader(String governmentLeader) {
        this.governmentLeader = governmentLeader;
    }

    public void setGovernmentMembers(List<String> governmentMembers) {
        this.governmentMembers = governmentMembers;
    }

    public String getArmyCommander() {
        return armyCommander;
    }

    public void setArmyCommander(String armyCommander) {
        this.armyCommander = armyCommander;
    }

    public void setArmyMembers(List<String> armyMembers) {
        this.armyMembers = armyMembers;
    }
}

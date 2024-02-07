package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("WDEorg")
public class WDEorg implements ConfigurationSerializable {
    private String organizationName;
    private UUID leaderUUID;
    private Set<UUID> members;
    private double balance;
    private Set<String> flags;
    private Map<String, Object> settings;

    public WDEorg(String organizationName, UUID leaderUUID) {
        this.organizationName = organizationName;
        this.leaderUUID = leaderUUID;
        this.members = new HashSet<>();
        this.balance = 0.0;
        this.flags = new HashSet<>();
        this.settings = new HashMap<>();
    }

    // Constructor used for deserialization
    public WDEorg(Map<String, Object> map) {
        this.organizationName = (String) map.get("organizationName");
        this.leaderUUID = UUID.fromString((String) map.get("leaderUUID"));
        this.members = new HashSet<>((Set<UUID>) map.get("members"));
        this.balance = (double) map.get("balance");
        this.flags = new HashSet<>((Set<String>) map.get("flags"));
        this.settings = (Map<String, Object>) map.get("settings");
    }

    // Method for serialization
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("organizationName", organizationName);
        map.put("leaderUUID", leaderUUID.toString());
        map.put("members", members);
        map.put("balance", balance);
        map.put("flags", flags);
        map.put("settings", settings);
        return map;
    }

    // Getters and Setters
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public UUID getLeaderUUID() {
        return leaderUUID;
    }

    public void setLeaderUUID(UUID leaderUUID) {
        this.leaderUUID = leaderUUID;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void setFlags(Set<String> flags) {
        this.flags = flags;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    // Helper methods
    public void addMember(UUID memberUUID) {
        members.add(memberUUID);
    }

    public void removeMember(UUID memberUUID) {
        members.remove(memberUUID);
    }

    public void addFlag(String flag) {
        flags.add(flag);
    }

    public void removeFlag(String flag) {
        flags.remove(flag);
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public Object getSetting(String key) {
        return settings.get(key);
    }

    public void setSetting(String key, Object value) {
        settings.put(key, value);
    }

    public static WDEorg deserialize(Map<String, Object> map) {
        return new WDEorg(map);
    }
}

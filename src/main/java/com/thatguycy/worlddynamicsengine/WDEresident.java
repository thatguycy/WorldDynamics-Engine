package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

@SerializableAs("WDEresident")
public class WDEresident implements ConfigurationSerializable {
    private UUID userUUID;
    private String username;
    private Set<String> flags;

    public WDEresident(UUID userUUID, String username) {
        this.userUUID = userUUID;
        this.username = username;
        this.flags = new HashSet<>();
    }

    // Constructor used for deserialization
    public WDEresident(Map<String, Object> map) {
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.username = (String) map.get("username");
        this.flags = new HashSet<>((Set<String>) map.get("flags"));
    }

    // Method for serialization
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUUID", userUUID.toString());
        map.put("username", username);
        map.put("flags", flags);
        return map;
    }


    // Getters and Setters
    public UUID getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void setFlags(Set<String> flags) {
        this.flags = flags;
    }

    // Helper methods
    public void addFlag(String flag) {
        flags.add(flag);
    }

    public void removeFlag(String flag) {
        flags.remove(flag);
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public static WDEresident deserialize(Map<String, Object> map) {
        return new WDEresident(map);
    }
}

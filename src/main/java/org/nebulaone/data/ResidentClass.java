package org.nebulaone.data;

import java.util.UUID;

public class ResidentClass {
    private UUID uuid;
    private String username;
    private String town;

    public ResidentClass(UUID uuid, String username, String town) {
        this.uuid = uuid;
        this.username = username;
        this.town = town;
    }

    // Getters and Setters
    public Object getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}

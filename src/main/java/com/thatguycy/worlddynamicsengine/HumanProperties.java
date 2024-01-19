package com.thatguycy.worlddynamicsengine;

public class HumanProperties {
    private final String username; // Minecraft username, immutable
    private String nickname; // Nickname, mutable
    private String occupation; // Occupation, mutable

    public HumanProperties(String username) {
        this.username = username;
        this.nickname = "none"; // Default value
        this.occupation = "none"; // Default value
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getOccupation() {
        return occupation;
    }

    // Setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}

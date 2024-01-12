package com.thatguycy.worlddynamicsengine;

import java.util.HashSet;
import java.util.Set;

public class OrganizationProperties {

    private String name;
    private String leader; // Player name for Business, Nation name for International
    private Set<String> members; // Player names or Nation names
    private OrganizationType type;

    public enum OrganizationType {
        BUSINESS,
        INTERNATIONAL
    }

    public OrganizationProperties(String name, String leader, OrganizationType type) {
        this.name = name;
        this.leader = leader;
        this.type = type;
        this.members = new HashSet<>();
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void addMember(String member) {
        members.add(member);
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    public OrganizationType getType() {
        return type;
    }
}

package com.thatguycy.worlddynamicsengine;

import java.util.HashSet;
import java.util.Set;

public class OrganizationProperties {

    private String name;
    private String leader; // Player name for Business, Nation name for International
    private Set<String> members; // Player names or Nation names
    private OrganizationType type;
    private double balance;

    public enum OrganizationType {
        BUSINESS,
        INTERNATIONAL,

        GOVERNMENTAL
    }

    public OrganizationProperties(String name, String leader, OrganizationType type) {
        this.name = name;
        this.leader = leader;
        this.type = type;
        this.members = new HashSet<>();
        this.balance = 0.0;
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


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
}

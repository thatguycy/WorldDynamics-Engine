package com.thatguycy.worlddynamicsengine;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class NewDayListener extends JavaPlugin implements Listener {
    private OrganizationManager organizationManager;

    public NewDayListener(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    // Utilize Townys NewDayEvent
    @EventHandler
    private void applyInterestToOrganizations(NewDayEvent event) {
        // Check if interest is enabled
        if (!getConfig().getBoolean("interest.enabled")) {
            return; // Exit if interest feature is disabled
        }

        double maxInterestRate = getConfig().getDouble("interest.maxrate");
        double minInterestRate = getConfig().getDouble("interest.minrate");
        Random random = new Random();

        for (OrganizationProperties org : organizationManager.getOrganizations().values()) {
            // Calculate a random interest rate between minrate and maxrate
            double interestRate = minInterestRate + (maxInterestRate - minInterestRate) * random.nextDouble();
            double interest = org.getBalance() * interestRate;
            org.deposit(interest);

            // Get the leader of the organization
            String leaderName = org.getLeader();
            Player leader = Bukkit.getPlayer(leaderName);

            // If the leader is online, send them the message
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(String.format("Your organization %s's balance has increased by %.2f%%! New Balance: %.2f",
                        org.getName(), interestRate * 100, org.getBalance()));
            }
        }
        organizationManager.saveOrganizations();
    }
}
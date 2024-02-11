package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.event.NewDayEvent; // Replace with actual Towny event
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Random;

public class TownyEventListener { // Or integrate into appropriate class

    private final OrgManager orgManager;

    // Constructor 
    public TownyEventListener(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    @EventHandler
    public void onTownyNewDay(NewDayEvent event) {
        List<WDEorg> orgs = orgManager.getAllOrganizations();
        Random random = new Random();

        for (WDEorg org : orgs) {
            double percentageChange = random.nextDouble(0.3) - 0.1;  // -10% to 20% range
            double modifier = 1 + percentageChange;
            double newBalance = org.getBalance() * modifier;

            orgManager.updateBalance(org, newBalance);

        }
    }
}
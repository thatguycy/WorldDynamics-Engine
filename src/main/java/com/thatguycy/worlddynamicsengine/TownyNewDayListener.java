package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.event.NewDayEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyNewDayListener implements Listener {

    private final WorldDynamicsEngine plugin;

    public TownyNewDayListener(WorldDynamicsEngine plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNewDay(NewDayEvent event) {
        plugin.applyInterestToOrganizations();
    }
}
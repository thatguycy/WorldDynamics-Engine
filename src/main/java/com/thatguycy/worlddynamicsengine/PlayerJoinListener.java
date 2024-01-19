package com.thatguycy.worlddynamicsengine;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final HumanManager humanManager;

    public PlayerJoinListener(HumanManager humanManager) {
        this.humanManager = humanManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String username = event.getPlayer().getName();
        if (humanManager.getHuman(username) == null) {
            HumanProperties human = new HumanProperties(username);
            humanManager.addHuman(human);
            humanManager.saveHumans();
        }
    }
}


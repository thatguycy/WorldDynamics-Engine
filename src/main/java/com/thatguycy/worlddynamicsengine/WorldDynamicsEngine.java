package com.thatguycy.worlddynamicsengine;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldDynamicsEngine extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("====================      WorldDynamics Engine      ========================");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldDynamics Engine is being disabled.");
    }

    public void checkForUpdates() {
        String pluginVersion = this.getDescription().getVersion();
    }
}

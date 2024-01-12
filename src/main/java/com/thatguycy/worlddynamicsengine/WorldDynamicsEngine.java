package com.thatguycy.worlddynamicsengine;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class WorldDynamicsEngine extends JavaPlugin {
    private NationManager nationManager;

    @Override
    public void onEnable() {
        nationManager = new NationManager(this);
        if (getServer().getPluginManager().getPlugin("Towny") == null ||
                getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("=============================================================");
            getLogger().info(" WorldDynamics Engine failed to start!");
            getLogger().info(" Reason: Vault/Towny not installed or started properly!");
            getLogger().info(" Notes: Open an issue request, or solve it yourself!");
            getLogger().info(" Version: " + this.getDescription().getVersion());
            getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().info("=============================================================");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            Plugin towny = getServer().getPluginManager().getPlugin("Towny");
            String townyVersion = towny.getDescription().getVersion();
            getLogger().info("=============================================================");
            getLogger().info(" WorldDynamics Engine has been successfully enabled!");
            getLogger().info(" Version: " + this.getDescription().getVersion());
            getLogger().info(" Towny Version: " + townyVersion);
            getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().info("=============================================================");
        }
        this.getCommand("wde").setExecutor(new WDECommandExecutor(nationManager));
        this.getCommand("wde").setTabCompleter(new WDETabCompleter());
        startGovernmentAutoSaveTask();
    }

    private void startGovernmentAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                nationManager.saveNations();
            }
        }.runTaskTimer(this, 1200L, 1200L); // 1200L = 60 seconds in ticks
    }

    @Override
    public void onDisable() {
        nationManager.saveNations();
    }
}

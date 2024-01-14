package com.thatguycy.worlddynamicsengine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.palmergames.bukkit.towny.TownyEconomyHandler.setupEconomy;

public final class WorldDynamicsEngine extends JavaPlugin {
    private NationManager nationManager;
    private OrganizationManager organizationManager;

    private static Economy economy = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        nationManager = new NationManager(this);
        organizationManager = new OrganizationManager(this.getDataFolder());
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
        this.getCommand("wde").setExecutor(new WDECommandExecutor(nationManager, organizationManager, getEconomy()));
        this.getCommand("wde").setTabCompleter(new WDETabCompleter(organizationManager));
        startGovernmentAutoSaveTask();
    }

    private void startGovernmentAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                nationManager.saveNations();
                organizationManager.saveOrganizations();
            }
        }.runTaskTimer(this, 1200L, 1200L); // 1200L = 60 seconds in ticks
    }

    @Override
    public void onDisable() {
        nationManager.saveNations();
        organizationManager.saveOrganizations();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
}

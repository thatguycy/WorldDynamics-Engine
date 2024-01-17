package com.thatguycy.worlddynamicsengine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.milkbowl.vault.economy.Economy;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

import java.util.Random;

import static com.palmergames.bukkit.towny.TownyEconomyHandler.setupEconomy;
import static org.bukkit.Bukkit.getServer;

public final class WorldDynamicsEngine extends JavaPlugin {
    private NationManager nationManager;
    private OrganizationManager organizationManager;

    private static Economy economy = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
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
        startDailyInterestTask();
    }


    private void startDailyInterestTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                applyInterestToOrganizations();
            }
        }.runTaskTimer(this, 0L, 24000L); // 24000L is the duration of a Minecraft day in ticks
    }

    private void applyInterestToOrganizations() {
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
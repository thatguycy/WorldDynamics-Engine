package com.thatguycy.worlddynamicsengine;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.io.InputStreamReader;
import java.io.Reader;

public class WorldDynamicsEngine extends JavaPlugin {
    private VersionManager versionManager;
    private boolean upToDate;

    String currentVersion  = versionManager.getCurrentVersion();
    String latestVersion = versionManager.getLatestVersion();
    @Override
    public void onEnable() {
        createOrUpdateConfig();
        checkForUpdates();
        // Plugin description
        Bukkit.getLogger().info("=====================================================================");
        Bukkit.getLogger().info(" WorldDynamics Engine - The Towny Essential Upgrade");
        Bukkit.getLogger().info(" Developed by thatguycy");
        Bukkit.getLogger().info("=====================================================================");

        // Dependency check for Vault and Towny
        if (getServer().getPluginManager().getPlugin("Vault") == null ||
                getServer().getPluginManager().getPlugin("Towny") == null) {
            Bukkit.getLogger().severe("Missing required plugins (Vault and/or Towny)! Disabling WorldDynamics Engine.");
            this.setEnabled(false);
            return;
        }

        // Version management
        versionManager = new VersionManager(this);
        String currentVersion = versionManager.getCurrentVersion();
        String latestVersion = versionManager.getLatestVersion();
        boolean upToDate = currentVersion.equals(latestVersion);

        Bukkit.getLogger().info("Your Version: " + currentVersion);
        Bukkit.getLogger().info("Latest Version: " + latestVersion);
        if (!upToDate) {
            Bukkit.getLogger().info("Consider updating WorldDynamics Engine to the latest version!");
        }

        // Misc
        new WDECommands(this);
    }


    @Override
    public void onDisable() {
        getLogger().info("WorldDynamics Engine is being disabled.");
    }

    // Version Management
    public void checkForUpdates() {
        upToDate = Objects.equals(currentVersion, latestVersion);
    }

    // Config Management
    private void createOrUpdateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        } else {
            mergeConfig(configFile);
        }
    }
    private void mergeConfig(File configFile) {
        FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);
        double configVersion = existingConfig.getDouble("config-version", 0.0);
        double currentVersion = 1.0; // Update this with each new version

        if (configVersion < currentVersion) {
            // Load the default config from the JAR
            Reader defConfigStream = new InputStreamReader(this.getResource("config.yml"));
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defConfigStream);

            // Update only the missing keys from the default config
            defaultConfig.getKeys(true).forEach(key -> {
                if (!existingConfig.contains(key)) {
                    existingConfig.set(key, defaultConfig.get(key));
                }
            });

            // Update the config version
            existingConfig.set("config-version", currentVersion);

            // Save the merged configuration
            try {
                existingConfig.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

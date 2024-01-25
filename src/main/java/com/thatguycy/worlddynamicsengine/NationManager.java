package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NationManager {
    private final JavaPlugin plugin;
    private File nationFile;
    private FileConfiguration nationData;
    private Map<String, WDEnation> nations;

    public NationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.nations = new HashMap<>();
        loadNations();
    }

    public void loadNations() {
        nationFile = new File(plugin.getDataFolder(), "data/nations.yml");
        if (!nationFile.exists()) {
            try {
                nationFile.getParentFile().mkdirs();
                nationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return; // If file creation fails, exit the method
            }
        }
        nationData = YamlConfiguration.loadConfiguration(nationFile);

        if (nationData == null) {
            plugin.getLogger().warning("Failed to load nation data.");
            return; // Exit if the configuration failed to load
        }
        for (String key : nationData.getKeys(false)) {
            plugin.getLogger().info("Attempting to load nation: " + key);
            try {
                WDEnation nation = (WDEnation) nationData.get(key);
                if (nation != null) {
                    nations.put(key, nation);
                    plugin.getLogger().info("Successfully loaded nation: " + key);
                } else {
                    plugin.getLogger().warning("Failed to load nation: " + key);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading nation '" + key + "': " + e.getMessage());
            }
        }

        plugin.getLogger().info("Total nations loaded: " + nations.size());
    }


    public void saveNations() {
        for (Map.Entry<String, WDEnation> entry : nations.entrySet()) {
            nationData.set(entry.getKey(), entry.getValue());
        }
        try {
            nationData.save(nationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNation(String name, WDEnation nation) {
        nations.put(name, nation);
        saveNations(); // Save immediately or you can implement a scheduled autosave
    }

    public WDEnation getNation(String name) {
        return nations.get(name);
    }

    public void enableAutoSave() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::saveNations, 12000L, 12000L); // Every 10 minutes
    }
}

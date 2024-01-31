package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public void updateNation(WDEnation nation) {
        if (nation != null && nations.containsKey(nation.getNationName())) {
            nations.put(nation.getNationName(), nation);
            saveNations();
        }
    }

    public void syncWithTowny() {
        // Get Towny nations
        List<String> townyNations = TownyUniverse.getInstance().getNations().stream()
                .map(nation -> nation.getName())
                .collect(Collectors.toList());

        // Sync Towny nations with your plugin's nations
        for (String townyNationName : townyNations) {
            if (!nations.containsKey(townyNationName)) {
                // Add new nation from Towny to your plugin
                nations.put(townyNationName, new WDEnation(townyNationName));
            }
        }

        // Remove nations that are no longer in Towny
        nations.keySet().removeIf(nationName -> !townyNations.contains(nationName));

        // Save changes to the .yaml file
        saveNations();
    }

    public void addNation(String name, WDEnation nation) {
        nations.put(name, nation);
        saveNations(); // Save immediately or you can implement a scheduled autosave
    }

    public WDEnation getNation(String name) {
        return nations.get(name);
    }

    public Location getCapitalSpawnLocation(WDEnation destinationNation) {
        try {
            TownyUniverse townyUniverse = TownyUniverse.getInstance();
            com.palmergames.bukkit.towny.object.Nation townyNation = townyUniverse.getNation(destinationNation.getNationName());

            if (townyNation != null) {
                Town capitalTown = townyNation.getCapital();
                if (capitalTown.hasSpawn()) {
                    return capitalTown.getSpawn();
                } else {
                    // Capital town does not have a spawn location set
                    System.out.println("The capital town of " + destinationNation.getNationName() + " does not have a spawn location set.");
                }
            } else {
                // Towny nation not found
                System.out.println("Nation not found in Towny: " + destinationNation.getNationName());
            }
        } catch (TownyException e) {
            // Handle Towny-specific exceptions, such as no town spawn
            System.out.println("Error getting capital spawn location for " + destinationNation.getNationName() + ": " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions
            System.out.println("Unexpected error while getting capital spawn location: " + e.getMessage());
        }
        return null;
    }

    public void enableAutoSave() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::saveNations, 12000L, 12000L); // Every 10 minutes
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::syncWithTowny, 6000L, 6000L); // Every 5 minutes
    }
}

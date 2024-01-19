package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HumanManager {
    private final Map<String, HumanProperties> humans; // Maps usernames to HumanProperties

    private File dataFolder;

    public HumanManager(File dataFolder) {
        this.humans = new HashMap<>();
        this.dataFolder = dataFolder;
        loadHumans(); // Load humans on initialization
    }

    public void saveHumans() {
        File humanFile = new File(dataFolder, "humans.yml");
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, HumanProperties> entry : humans.entrySet()) {
            String path = "humans." + entry.getKey();
            HumanProperties human = entry.getValue();
            config.set(path + ".nickname", human.getNickname());
            config.set(path + ".occupation", human.getOccupation());
            // Add other properties as needed
        }

        try {
            config.save(humanFile);
        } catch (IOException e) {
            e.printStackTrace(); // Handle this appropriately
        }
    }

    public void loadHumans() {
        File humanFile = new File(dataFolder, "humans.yml");
        if (!humanFile.exists()) {
            return; // No file to load
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(humanFile);
        ConfigurationSection humanSection = config.getConfigurationSection("humans");
        if (humanSection != null) {
            for (String username : humanSection.getKeys(false)) {
                String path = "humans." + username;
                String nickname = config.getString(path + ".nickname", "none");
                String occupation = config.getString(path + ".occupation", "none");
                HumanProperties human = new HumanProperties(username);
                human.setNickname(nickname);
                human.setOccupation(occupation);
                humans.put(username, human);
            }
        }
    }

    public HumanManager() {
        humans = new HashMap<>();
    }

    // Add a new human
    public void addHuman(HumanProperties human) {
        humans.put(human.getUsername(), human);
    }

    // Get a human's properties
    public HumanProperties getHuman(String username) {
        return humans.get(username);
    }

    // Remove a human
    public void removeHuman(String username) {
        humans.remove(username);
    }
}

package org.nebulaone.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {

    private final File dataFolder;

    // [thatguycy] Constructor to initialize DataManager with the data folder. ({1/10/2024}/{0.3.0})
    public DataManager(File dataFolder) {
        this.dataFolder = dataFolder;

        // [thatguycy] Ensure the data folder exists or create it. ({1/10/2024}/{0.3.0})
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    // [thatguycy] Save resident data to a YAML file. ({1/10/2024}/{0.3.0})
    public void saveResident(ResidentClass resident) {
        File file = new File(dataFolder, resident.getUUID().toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("uuid", resident.getUUID().toString());
        config.set("username", resident.getUsername());
        config.set("town", resident.getTown());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // [thatguycy] Load (get) resident data from a YAML file. ({1/10/2024}/{0.3.0})
    public ResidentClass getResident(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (!file.exists()) {
            return null; // [thatguycy] No resident data found for this UUID. ({1/10/2024}/{0.3.0})
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        UUID residentUUID = UUID.fromString(config.getString("uuid"));
        String username = config.getString("username");
        String town = config.getString("town");

        return new ResidentClass(residentUUID, username, town);
    }

    // [thatguycy] Delete a resident's data file. ({1/10/2024}/{0.3.0})
    public void deleteResident(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }
}

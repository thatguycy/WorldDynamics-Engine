package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.thatguycy.worlddynamicsengine.WDEresident;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResidentManager {

    private final JavaPlugin plugin;
    private File residentFile;
    private FileConfiguration residentData;
    private Map<UUID, WDEresident> residents;

    public ResidentManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.residents = new HashMap<>();
        loadResidents();
    }

    public void loadResidents() {
        residentFile = new File(plugin.getDataFolder(), "data/residents.yml");
        if (!residentFile.exists()) {
            try {
                residentFile.getParentFile().mkdirs();
                residentFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        residentData = YamlConfiguration.loadConfiguration(residentFile);

        if (residentData == null) {
            plugin.getLogger().warning("Failed to load resident data.");
            return;
        }
        for (String key : residentData.getKeys(false)) {
            WDEresident resident = (WDEresident) residentData.get(key);
            if (resident != null) {
                residents.put(UUID.fromString(key), resident);
            }
        }

        plugin.getLogger().info("Total residents loaded: " + residents.size());
    }

    public void saveResidents() {
        for (Map.Entry<UUID, WDEresident> entry : residents.entrySet()) {
            residentData.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            residentData.save(residentFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WDEresident getResident(UUID userUUID) {
        return residents.get(userUUID);
    }

    public void addResident(WDEresident resident) {
        residents.put(resident.getUserUUID(), resident);
    }


    public void enableAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveResidents();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L * 60 * 5); // Autosave every 5 minutes, adjust as needed
    }

    public void syncWithTowny() {
        for (Resident townyResident : TownyUniverse.getInstance().getResidents()) {
            UUID uuid = townyResident.getUUID();
            if (!residents.containsKey(uuid)) {
                WDEresident newResident = new WDEresident(uuid, townyResident.getName());
                addResident(newResident);
            }
        }
    }
}

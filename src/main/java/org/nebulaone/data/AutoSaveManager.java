package org.nebulaone.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.nebulaone.data.DataManager;
import org.nebulaone.data.ResidentClass;

import java.util.logging.Logger;

public class AutoSaveManager {

    private final DataManager dataManager;
    private final Plugin plugin;
    private final Logger logger;

    public AutoSaveManager(DataManager dataManager, Plugin plugin) {
        this.dataManager = dataManager;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    // [thatguycy] Start AutoSave task every 5 minutes (6000 ticks). ({1/10/2024}/{0.3.0})
    public void startAutoSave() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::saveAllData, 6000L, 6000L); // 5 minutes in ticks
        logger.info("AutoSaveManager started, saving all data every 5 minutes.");
    }

    // [thatguycy] Save all player data manually. ({1/10/2024}/{0.3.0})
    public void saveAllData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ResidentClass resident = new ResidentClass(player.getUniqueId(), player.getName(), "defaultTown"); // You can change this to actual town data
            dataManager.saveResident(resident);
            logger.info("Auto-saved data for player: " + player.getName());
        }
    }

    // [thatguycy] Save data for a specific player (for example, on leave). ({1/10/2024}/{0.3.0})
    public void savePlayerData(Player player) {
        ResidentClass resident = new ResidentClass(player.getUniqueId(), player.getName(), "defaultTown");
        dataManager.saveResident(resident);
        logger.info("Auto-saved data for player: " + player.getName());
    }
}

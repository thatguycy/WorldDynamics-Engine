package org.nebulaone;

import org.bukkit.plugin.java.JavaPlugin;
import org.nebulaone.commands.ResidentCommand;
import org.nebulaone.commands.WDECommandExecutor;
import org.nebulaone.commands.WDETabCompleter;
import org.nebulaone.data.AutoSaveManager;
import org.nebulaone.data.DataManager;
import org.nebulaone.listeners.ResidentInventoryListener;

public final class InitClass extends JavaPlugin {

    private DataManager dataManager;
    private AutoSaveManager autoSaveManager;

    @Override
    public void onEnable() {
        getLogger().info("WorldDynamics Engine v0.3.0 BETA has been enabled.");
        getLogger().warning("You are on a BETA/ALPHA/DEV Version. Expect Bugs!");

        dataManager = new DataManager(getDataFolder());

        // Register the /wde resident command
        getCommand("wde").setExecutor(new WDECommandExecutor());
        getCommand("wde").setTabCompleter(new WDETabCompleter());

        // [thatguycy] Register the ResidentCommand for /wde resident ({1/10/2024}/{0.3.0})
        if (getCommand("wde") != null) {
            getCommand("wde").setExecutor(new WDECommandExecutor());
        }

        autoSaveManager = new AutoSaveManager(dataManager, this);
        autoSaveManager.startAutoSave();

        getServer().getPluginManager().registerEvents(new ResidentInventoryListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldDynamics Engine v0.3.0 BETA has been disabled.");
    }
}

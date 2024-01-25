package com.thatguycy.worlddynamicsengine;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldDynamicsEngine extends JavaPlugin {
    private Economy economy;
    @Override
    public void onEnable() {
        if (!checkDependencies()) {
            getLogger().severe("Missing required dependencies. Disabling WorldDynamics Engine.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                String latestVersion = fetchLatestVersion();
                getLogger().info("=================== WorldDynamics Engine ===================");
                getLogger().info("Author: thatguycy");
                getLogger().info("Contributor(s): 1ByteBit");
                getLogger().info("Version: 0.2.0");
                getLogger().info("Latest Version: " + latestVersion);
                getLogger().info("Crafting Complex Worlds, Shaping Geopolitical Adventures.");
                getLogger().info("============================================================");
            }
        }.runTaskAsynchronously(this);
    }

    private boolean checkDependencies() {
        if (getServer().getPluginManager().getPlugin("Towny") == null ||
                getServer().getPluginManager().getPlugin("Vault") == null ||
                !setupEconomy()) {
            return false;
        }
        return true;
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

    private String fetchLatestVersion() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/WorldDynamics-MC/WorldDynamics-Engine/v0.2.x/current.version");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            httpURLConnection.disconnect();

            return content.toString().trim();
        } catch (Exception e) {
            getLogger().warning("Failed to fetch the latest version: " + e.getMessage());
            return "Unknown";
        }
    }
    @Override
    public void onDisable() {

    }
}
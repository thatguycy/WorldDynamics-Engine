package com.thatguycy.worlddynamicsengine;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WorldDynamicsEngine extends JavaPlugin {
    private CommandHandler commandHandler;
    private Economy economy;
    private NationManager nationManager;
    private YamlDocument config;
    private String framework;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(WDEnation.class);
        commandHandler = new CommandHandler(this);
        nationManager = new NationManager(this);

        // Use BoostedYAML for automatic config updating
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get config settings
        getConfigSettings();

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

        // Commands
        commandHandler.registerSubCommand("help", new HelpCommand());
        commandHandler.registerSubCommand("docs", new DocCommand());

        // Misc
        nationManager.enableAutoSave();
    }

    private void getConfigSettings() {
        framework = getConfig().getString("framework");
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
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
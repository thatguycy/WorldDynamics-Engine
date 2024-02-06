package com.thatguycy.worlddynamicsengine;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldDynamicsEngine extends JavaPlugin {
    private static WorldDynamicsEngine instance;
    private CommandHandler commandHandler;
    private Economy economy;
    private NationManager nationManager;
    private ResidentManager residentManager;
    private YamlDocument config;
    public String framework;
    public boolean govEnabled;
    public boolean orgEnabled;
    public double diplomacyVisitCostNeutralNone;
    public double diplomacyVisitCostFriendly;
    public double diplomacyFormCostTrade;
    public double orgFormationCost;
    public boolean orgBusinessTownLocked;
    public int residentOrgLimit;
    public String userLang;
    private final Map<String, FileConfiguration> locales = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        ConfigurationSerialization.registerClass(WDEnation.class);
        ConfigurationSerialization.registerClass(WDEresident.class, "WDEresident");
        commandHandler = new CommandHandler(this);
        nationManager = new NationManager(this);
        residentManager = new ResidentManager(this);

        // Load Locales

        saveDefaultLocale("messages_en.yml");
        loadLocales();

        // Config Stuff

        createConfig();
        getConfigSettings();

        if (!checkDependencies()) {
            getLogger().severe("Missing required dependencies. Disabling WorldDynamics Engine.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                getLogger().info("=================== WorldDynamics Engine ===================");
                getLogger().info("Author: thatguycy");
                getLogger().info("Contributor(s): 1ByteBit");
                getLogger().info("Version: 0.2.0");
                getLogger().info("Latest Version: " + fetchLatestVersion());
                getLogger().info("Crafting Complex Worlds, Shaping Geopolitical Adventures.");
                getLogger().info("============================================================");
            }
        }.runTaskAsynchronously(this);

        // Commands
        commandHandler.registerSubCommand("help", new HelpCommand());
        commandHandler.registerSubCommand("docs", new DocCommand());
        commandHandler.registerSubCommand("nation", new NationCommand(nationManager, residentManager));
        commandHandler.registerSubCommand("diplomacy", new DiplomacyCommand(nationManager, residentManager, economy));

        this.getCommand("wde").setTabCompleter(new MyTabCompleter());

        // Misc
        nationManager.syncWithTowny();
        nationManager.enableAutoSave();
        residentManager.syncWithTowny();
        residentManager.enableAutoSave();
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static WorldDynamicsEngine getInstance() {
        return instance;
    }
    private void getConfigSettings() {
        framework = getConfig().getString("framework");
        govEnabled = getConfig().getBoolean("govEnabled");
        orgEnabled = getConfig().getBoolean("orgEnabled");
        diplomacyVisitCostNeutralNone = getConfig().getDouble("diplomacyVisitCostNeutralNone");
        diplomacyVisitCostFriendly = getConfig().getDouble("diplomacyVisitCostFriendly");
        diplomacyFormCostTrade = getConfig().getDouble("diplomacyFormCostTrade");
        orgFormationCost = getConfig().getDouble("orgFormationCost");
        orgBusinessTownLocked = getConfig().getBoolean("orgBusinessTownLocked");
        residentOrgLimit = getConfig().getInt("residentOrgLimit");
        userLang = getConfig().getString("locale");
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
        if (checkDependencies()) {
            try {
                config.save();
                nationManager.syncWithTowny();
                residentManager.syncWithTowny();
                residentManager.saveResidents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void saveDefaultLocale(String fileName) {
        File localeFile = new File(getDataFolder(), fileName);
        if (!localeFile.exists()) {
            saveResource(fileName, false);
        }
    }

    private void loadLocales() {
        File folder = getDataFolder();
        if (folder.listFiles() == null) return;
        for (File file : folder.listFiles()) {
            if (file.getName().startsWith("messages_")) {
                locales.put(file.getName().substring(9, 11), YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    public String getLocaleMessage(String locale, String key) {
        FileConfiguration config = locales.get(locale);
        if (config == null) {
            config = locales.get("en"); // Default to English if the requested locale is not loaded
        }
        return config.getString(key, "Message not found"); // Return a default message if the key is not found
    }
}
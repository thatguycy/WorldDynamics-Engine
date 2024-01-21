package com.thatguycy.worlddynamicsengine;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.object.AddonCommand;
import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import io.github.townyadvanced.commentedconfiguration.setting.Settings;
import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.milkbowl.vault.economy.Economy;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.palmergames.bukkit.towny.TownyEconomyHandler.setupEconomy;
import static org.bukkit.Bukkit.getServer;

public final class WorldDynamicsEngine extends JavaPlugin {
    private NationManager nationManager;
    private HumanManager humanManager;

    private OrganizationManager organizationManager;

    private static Economy economy = null;
    private boolean organizationsEnabled;
    private boolean armyEnabled;
    private boolean governmentEnabled;
    private static int lawVoteTime;
    private Settings settings;
    private int autoSaveInterval;

    @Override
    public void onEnable() {
        Path configPath = this.getDataFolder().toPath().resolve("config.yml");
        List<TypedValueNode<?>> defaultNodes = Arrays.asList(
                INTEREST,
                INTEREST_ENABLED,
                INTEREST_MAXRATE,
                INTEREST_MINRATE,
                GOVERNMENT_TYPES,
                ORGANIZATIONS,
                ORGANIZATIONS_ENABLED,
                ARMY,
                ARMY_ENABLED,
                GOVERNMENT,
                GOVERNMENT_ENABLED,
                GOVERNMENT_LAW_VOTE_TIME,
                CONFIG_VERSION,
                MISC,
                MISC_AUTO_SAVE
        );

        settings = new Settings(configPath, this, defaultNodes);

        if (!settings.load()) {
            getLogger().severe("Failed to load configuration!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("Towny") == null ||
                getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("=============================================================");
            getLogger().warning(" WorldDynamics Engine failed to start!");
            getLogger().warning(" Reason: Vault/Towny not installed or started properly!");
            getLogger().warning(" Notes: Open an issue request, or solve it yourself!");
            getLogger().warning(" Version: " + this.getDescription().getVersion());
            getLogger().warning(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().warning("=============================================================");
            getServer().getPluginManager().disablePlugin(this);
        } else if (!setupEconomy()) {
            getLogger().warning("=============================================================");
            getLogger().warning(" WorldDynamics Engine failed to start!");
            getLogger().warning(" Reason: Unable to setup economy!");
            getLogger().warning(" Notes: Try adding an economy plugin, or report this issue!");
            getLogger().warning(" Version: " + this.getDescription().getVersion());
            getLogger().warning(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().warning("=============================================================");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            Plugin towny = getServer().getPluginManager().getPlugin("Towny");
            String townyVersion = towny.getDescription().getVersion();
            getLogger().info("=============================================================");
            getLogger().info(" WorldDynamics Engine has been successfully enabled!");
            getLogger().info(" Version: " + this.getDescription().getVersion());
            getLogger().info(" Towny Version: " + townyVersion);
            getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().info("=============================================================");
        }
        // Get config settings
        organizationsEnabled = getConfig().getBoolean("organizations.enabled", true); // Default to true
        armyEnabled = getConfig().getBoolean("army.enabled", true); // Default to true
        governmentEnabled = getConfig().getBoolean("government.enabled", true); // Default to true
        lawVoteTime = getConfig().getInt("government.law-vote-time", 120); // Default to 120 seconds
        autoSaveInterval = getConfig().getInt("misc.auto-save-interval", 60) * 20; // Default to 60 seconds, multiply by tickrate
        // Initialize other classes
        loadGovernmentTypes();
        nationManager = new NationManager(this);
        organizationManager = new OrganizationManager(this.getDataFolder());
        humanManager = new HumanManager(this.getDataFolder());
        VotingManager votingManager = new VotingManager(nationManager, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(humanManager), this);
        getServer().getPluginManager().registerEvents(new TownyNewDayListener(this), this);
        checkForUpdates();
        // Setup command executor, tab completion and register a towny subcommand
        CommandExecutor commandExecutor = new WDECommandExecutor(nationManager, organizationManager, getEconomy(), this, humanManager, votingManager);
        this.getCommand("wde").setExecutor(commandExecutor);
        this.getCommand("wde").setTabCompleter(new WDETabCompleter(organizationManager));
        TownyCommandAddonAPI.addSubCommand(TownyCommandAddonAPI.CommandType.TOWNY, "wde", commandExecutor);
        Metrics metrics = new Metrics(this, 20763); // Bstats
        startGovernmentAutoSaveTask();
    }
    public HumanManager getHumanManager() {
        return humanManager;
    }
    public boolean isOrganizationsEnabled() {
        return organizationsEnabled;
    }

    public boolean isArmyEnabled() {
        return armyEnabled;
    }

    public boolean isGovernmentEnabled() {
        return governmentEnabled;
    }

    private void checkAndUpdateConfig() {
        String currentConfigVersion = "0.1.8"; // Update this with each config layout change
        if (!getConfig().contains("config-version")) {
            // Config version key doesn't exist, so it's an old or default config
            updateConfig(currentConfigVersion);
        } else {
            String configVersion = getConfig().getString("config-version", "0.0");
            if (!configVersion.equals(currentConfigVersion)) {
                // Config is outdated, update it
                updateConfig(currentConfigVersion);
            }
        }
    }


    private void mergeConfig() throws IOException {
        // Load the default configuration from the plugin's resources
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("config.yml")));

        // Load the actual configuration from the server's plugin data folder
        CommentedConfiguration actualConfig = (CommentedConfiguration) CommentedConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        boolean configUpdated = false;

        for (String key : defaultConfig.getKeys(true)) {
            if (!actualConfig.contains(key)) {
                actualConfig.set(key, defaultConfig.get(key));
                configUpdated = true;
            }
        }

        if (configUpdated) {
            // Save the updated configuration back to the file
            actualConfig.save(new File(getDataFolder(), "config.yml"));
        }
    }

    private void updateConfig(String newVersion) {
        File configFile = new File(getDataFolder(), "config.yml");
        File backupFile = new File(getDataFolder(), "config-backup-" + System.currentTimeMillis() + ".yml");

        try {
            // Backup the old config
            if (configFile.exists()) {
                Files.copy(configFile.toPath(), backupFile.toPath());
            }

            getConfig().set("config-version", newVersion);

            saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Failed to update the config file!");
        }
    }

    public static int lawVotingTime(){return lawVoteTime;}
    private void loadGovernmentTypes() {
        List<String> types = getConfig().getStringList("government_types");
        GovernmentType.loadTypes(new HashSet<>(types));
    }
    void applyInterestToOrganizations() {
        // Check if interest is enabled
        if (!getConfig().getBoolean("interest.enabled")) {
            return; // Exit if interest feature is disabled
        }

        double maxInterestRate = getConfig().getDouble("interest.maxrate");
        double minInterestRate = getConfig().getDouble("interest.minrate");
        Random random = new Random();

        for (OrganizationProperties org : organizationManager.getOrganizations().values()) {
            // Calculate a random interest rate between minrate and maxrate
            double interestRate = minInterestRate + (maxInterestRate - minInterestRate) * random.nextDouble();
            double interest = org.getBalance() * interestRate;
            org.deposit(interest);

            // Get the leader of the organization
            String leaderName = org.getLeader();
            Player leader = Bukkit.getPlayer(leaderName);

            // If the leader is online, send them the message
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(String.format("Your organization %s's balance has increased by %.2f%%! New Balance: %.2f",
                        org.getName(), interestRate * 100, org.getBalance()));
            }
        }
        organizationManager.saveOrganizations();
    }

    private void startGovernmentAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                nationManager.saveNations();
                organizationManager.saveOrganizations();
                humanManager.saveHumans();
            }
        }.runTaskTimer(this, autoSaveInterval, autoSaveInterval);
    }

    @Override
    public void onDisable() {
        nationManager.saveNations();
        organizationManager.saveOrganizations();
        humanManager.saveHumans();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
    private void checkForUpdates() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/thatguycy/WorldDynamics-Engine/master/current.version"); // URL to your version file
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = in.readLine();
            in.close();

            String currentVersion = this.getDescription().getVersion();
            if (!currentVersion.equals(latestVersion)) {
                getLogger().info("=============================================================");
                getLogger().info(" WorldDynamics Engine is out of date!");
                getLogger().info(" Your Version: " + currentVersion);
                getLogger().info(" Our Version: " + latestVersion);
                getLogger().info(" Update: https://github.com/thatguycy/WorldDynamics-Engine");
                getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
                getLogger().info("=============================================================");
            }
        } catch (Exception e) {
            getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
    }

}
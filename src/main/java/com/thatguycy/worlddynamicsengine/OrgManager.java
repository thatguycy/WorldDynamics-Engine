package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrgManager {

    private final JavaPlugin plugin;
    private File orgFile;
    private FileConfiguration orgData;
    private Map<String, WDEorg> organizations;

    public OrgManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.organizations = new HashMap<>();
        loadOrganizations();
    }

    public void loadOrganizations() {
        orgFile = new File(plugin.getDataFolder(), "data/organizations.yml");
        if (!orgFile.exists()) {
            try {
                orgFile.getParentFile().mkdirs();
                orgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        orgData = YamlConfiguration.loadConfiguration(orgFile);

        if (orgData == null) {
            plugin.getLogger().warning("Failed to load organization data.");
            return;
        }
        for (String key : orgData.getKeys(false)) {
            WDEorg organization = (WDEorg) orgData.get(key);
            if (organization != null) {
                organizations.put(key, organization);
            }
        }

        plugin.getLogger().info("Total organizations loaded: " + organizations.size());
    }

    public void saveOrganizations() {
        for (Map.Entry<String, WDEorg> entry : organizations.entrySet()) {
            orgData.set(entry.getKey(), entry.getValue());
        }
        try {
            orgData.save(orgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WDEorg getOrganization(String organizationName) {
        return organizations.get(organizationName);
    }

    public void addOrganization(WDEorg organization) {
        organizations.put(organization.getOrganizationName(), organization);
    }

    public void enableAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveOrganizations();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L * 60 * 5); // Autosave every 5 minutes, adjust as needed
    }
}

package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public List<String> getAllOrganizationNames() {
        List<String> orgNames = new ArrayList<>();
        if (organizations.size() > 0) { // Ensure there are organizations to return
            orgNames.addAll(organizations.keySet());
        }
        return orgNames;
    }

    public void addOrganization(WDEorg organization) {
        organizations.put(organization.getOrganizationName(), organization);
    }

    public List<WDEorg> getAllOrganizations() {
        return new ArrayList<>(organizations.values()); // Return a copy of the organizations
    }

    public void updateBalance(WDEorg org, double newBalance) {
        // Error Handling (Ensure the organization  actually exists...)
        if (!organizations.containsKey(org.getOrganizationName())) {
            // Log or handle  the scenario where the org isn't found...
            return;
        }

        org.setBalance(newBalance);
    }

    public void removeOrganization(WDEorg organization) {
        organizations.remove(organization.getOrganizationName(), organization);
    }

    // Membership Management
    public void addMember(WDEorg org, UUID playerUUID) {
        org.getMembers().add(playerUUID);
    }

    public boolean removeMember(WDEorg org, UUID playerUUID) {
        org.getMembers().remove(playerUUID);
        return true;
    }


    public boolean isMember(WDEorg org, UUID playerUUID) {
        return org.getMembers().contains(playerUUID);
    }

    // Finance Management
    public void setBalance(WDEorg org, double newBalance) {
        org.setBalance(newBalance);
    }

    // Helper/Retrieval Methods
    public boolean organizationExists(String orgName) {
        return organizations.containsKey(orgName);
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

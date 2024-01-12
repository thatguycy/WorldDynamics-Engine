package com.thatguycy.worlddynamicsengine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class OrganizationManager {

    private Map<String, OrganizationProperties> organizations;
    private File organizationsFile;
    private FileConfiguration organizationsConfig;

    public OrganizationManager(File dataFolder) {
        this.organizations = new HashMap<>();
        this.organizationsFile = new File(dataFolder, "organizations.yml");
        this.organizationsConfig = YamlConfiguration.loadConfiguration(organizationsFile);
        loadOrganizations();
    }

    private void loadOrganizations() {
        if (organizationsConfig.isConfigurationSection("organizations")) {
            for (String orgName : organizationsConfig.getConfigurationSection("organizations").getKeys(false)) {
                String leader = organizationsConfig.getString("organizations." + orgName + ".leader");
                Set<String> members = new HashSet<>(organizationsConfig.getStringList("organizations." + orgName + ".members"));
                OrganizationProperties.OrganizationType type = OrganizationProperties.OrganizationType.valueOf(
                        organizationsConfig.getString("organizations." + orgName + ".type"));

                OrganizationProperties orgProps = new OrganizationProperties(orgName, leader, type);
                orgProps.getMembers().addAll(members);
                organizations.put(orgName, orgProps);
            }
        }
    }

    public void saveOrganizations() {
        for (Map.Entry<String, OrganizationProperties> entry : organizations.entrySet()) {
            String orgName = entry.getKey();
            OrganizationProperties orgProps = entry.getValue();

            organizationsConfig.set("organizations." + orgName + ".leader", orgProps.getLeader());
            organizationsConfig.set("organizations." + orgName + ".members", new ArrayList<>(orgProps.getMembers()));
            organizationsConfig.set("organizations." + orgName + ".type", orgProps.getType().name());
        }

        try {
            organizationsConfig.save(organizationsFile);
        } catch (IOException e) {
            e.printStackTrace(); // Handle this appropriately
        }
    }

    public OrganizationManager() {
        this.organizations = new HashMap<>();
    }

    public void addOrganization(String name, OrganizationProperties organization) {
        organizations.put(name, organization);
    }

    public void removeOrganization(String name) {
        organizations.remove(name);
    }

    public OrganizationProperties getOrganization(String name) {
        return organizations.get(name);
    }

    // Additional methods for saving and loading organization data
}

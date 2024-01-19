package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.thatguycy.worlddynamicsengine.GovernmentType;
import com.thatguycy.worlddynamicsengine.NationProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
public class NationManager {

    private JavaPlugin plugin;
    private Map<String, NationProperties> nations;
    private File nationsFile;
    private FileConfiguration nationsConfig;

    public NationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.nations = new HashMap<>();
        this.nationsFile = new File(plugin.getDataFolder(), "nations.yml");
        this.nationsConfig = YamlConfiguration.loadConfiguration(nationsFile);
        loadNations();
    }

    private void loadNations() {
        if (nationsConfig.isConfigurationSection("nations")) {
            for (String nationName : nationsConfig.getConfigurationSection("nations").getKeys(false)) {
                // Load GovernmentType
                String govTypeStr = nationsConfig.getString("nations." + nationName + ".GovernmentType", "DEMOCRACY");

                // Load Army Leader and Members
                String armyLeaderName = nationsConfig.getString("nations." + nationName + ".ArmyLeader", null);
                Resident armyLeader = armyLeaderName != null ? TownyUniverse.getInstance().getResident(armyLeaderName) : null;
                List<String> armyMembers = nationsConfig.getStringList("nations." + nationName + ".ArmyMembers");

                NationProperties properties = new NationProperties(govTypeStr);
                properties.setArmyLeader(armyLeader);
                armyMembers.forEach(properties::addArmyMember);

                nations.put(nationName, properties);
            }
        }
    }

    public void saveNations() {
        for (Map.Entry<String, NationProperties> entry : nations.entrySet()) {
            String nationName = entry.getKey();
            NationProperties properties = entry.getValue();

            // Save GovernmentType
            nationsConfig.set("nations." + nationName + ".GovernmentType", properties.getGovernmentType());

            // Save Army Leader and Members
            if (properties.getArmyLeader() != null) {
                nationsConfig.set("nations." + nationName + ".ArmyLeader", properties.getArmyLeader().getName());
            }
            nationsConfig.set("nations." + nationName + ".ArmyMembers", new ArrayList<>(properties.getArmyMembers()));

            // Save other properties as needed
        }
        try {
            nationsConfig.save(nationsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NationProperties getNationProperties(String nationName) {
        return nations.get(nationName);
    }

    public void setNationProperties(String nationName, NationProperties properties) {
        nations.put(nationName, properties);
    }

    public void displayNationInfo(CommandSender sender, String nationName) {
        boolean nationExistsInTowny = TownyUniverse.getInstance().hasNation(nationName);
        NationProperties properties = getNationProperties(nationName);

        // Header
        String header = ChatColor.GOLD + "---------------=[" + ChatColor.GREEN + " " + nationName + " " + ChatColor.GOLD + "]=---------------";
        sender.sendMessage(header);

        if (nationExistsInTowny) {
            if (properties != null) {
                // Display nation info from NationManager
                String governmentType = properties.getGovernmentType() != null ? properties.getGovernmentType().toString() : "none";
                sender.sendMessage(ChatColor.YELLOW + "Government Type: " + ChatColor.WHITE + governmentType);
                String leaderName = properties.getArmyLeader() != null ? properties.getArmyLeader().getName() : "None";
                sender.sendMessage(ChatColor.YELLOW + "Army Leader: " + ChatColor.WHITE + leaderName);
                String members = properties.getArmyMembers().stream().collect(Collectors.joining(", "));
                sender.sendMessage(ChatColor.YELLOW + "Army Members: " + ChatColor.WHITE + (members.isEmpty() ? "None" : members));
                String gleaderName = properties.getGovernmentLeader() != null ? properties.getGovernmentLeader().getName() : "None";
                sender.sendMessage(ChatColor.YELLOW + "Government Leader: " + ChatColor.WHITE + gleaderName);
                String gmembers = properties.getGovernmentMembers().stream().collect(Collectors.joining(", "));
                sender.sendMessage(ChatColor.YELLOW + "Government Members: " + ChatColor.WHITE + (gmembers.isEmpty() ? "None" : gmembers));

            } else {
                // Nation exists in Towny but not in WDE
                sender.sendMessage(ChatColor.RED + "This nation exists, but does not have any attributes assigned by WDE.");
            }
        } else {
            // Nation does not exist
            sender.sendMessage(ChatColor.RED + "Nation does not exist.");
        }
    }
}

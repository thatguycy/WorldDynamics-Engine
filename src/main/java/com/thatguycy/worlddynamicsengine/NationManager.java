package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.thatguycy.worlddynamicsengine.GovernmentType;
import com.thatguycy.worlddynamicsengine.NationProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                String govTypeStr = nationsConfig.getString("nations." + nationName + ".GovernmentType", "DEMOCRACY");
                GovernmentType govType = GovernmentType.valueOf(govTypeStr);
                // Load other properties as needed
                nations.put(nationName, new NationProperties(govType));
            }
        }
    }

    public void saveNations() {
        for (Map.Entry<String, NationProperties> entry : nations.entrySet()) {
            nationsConfig.set("nations." + entry.getKey() + ".GovernmentType", entry.getValue().getGovernmentType().name());
            // Save other properties
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
                String governmentType = properties.getGovernmentType() != null ? properties.getGovernmentType().name() : "none";
                sender.sendMessage(ChatColor.YELLOW + "Government Type: " + ChatColor.WHITE + governmentType);
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

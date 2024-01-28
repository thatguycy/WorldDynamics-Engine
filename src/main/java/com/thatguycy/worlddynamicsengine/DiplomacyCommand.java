package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DiplomacyCommand implements CommandExecutor {

    private NationManager nationManager;

    public DiplomacyCommand(NationManager nationManager) {
        this.nationManager = nationManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wde")) {
            // Ensure there are enough arguments for the diplomacy commands
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Insufficient arguments.");
                return true;
            }

            if (args[0].equalsIgnoreCase("diplomacy")) {
                String nationName = args[2];
                WDEnation nation = nationManager.getNation(nationName);

                if (nation == null) {
                    sender.sendMessage(ChatColor.RED + "Nation not found: " + nationName);
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "viewtrading":
                        // handleViewTradingRelations requires CommandSender, nationName, and plugin instance
                        return handleViewTradingRelations(sender, nationName, WorldDynamicsEngine.getInstance());
                    case "relations":
                        // handleViewDiplomaticRelations requires CommandSender, nationName, and plugin instance
                        return handleViewDiplomaticRelations(sender, nationName, WorldDynamicsEngine.getInstance());
                    case "setrelation":
                        if (args.length < 4) {
                            sender.sendMessage(ChatColor.RED + "Usage: /wde diplomacy setrelation <nationName> <relation>");
                            return true;
                        }
                        // Pass the target nation's name as the second argument
                        return handleSetRelation(sender, args[2], args, WorldDynamicsEngine.getInstance());

                    case "trading":
                        if (args.length < 4) {
                            sender.sendMessage(ChatColor.RED + "Usage: /wde diplomacy trading <nationName> <enabled/disabled>");
                            return true;
                        }
                        // Pass the target nation's name as the second argument
                        return handleSetTrading(sender, args[2], args, WorldDynamicsEngine.getInstance());

                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                        return true;
                }
            }
            // Handle other commands...
        }

        // If the command is not recognized
        sender.sendMessage(ChatColor.RED + "Unknown command.");
        return true;
    }
    private boolean handleSetRelation(CommandSender sender, String targetNationName, String[] args, JavaPlugin plugin) {
        // Fetch the sender's nation
        Nation senderNation = getNationByResident(sender.getName());
        if (senderNation == null) {
            sender.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
            return true;
        }

        // Check diplomatic authority
        if (!hasDiplomaticAuthority(sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have authority to set diplomatic relations.");
            return true;
        }

        String relation = args[3].toLowerCase();
        if (!Arrays.asList("friendly", "neutral", "unfriendly").contains(relation)) {
            sender.sendMessage(ChatColor.RED + "Invalid relation. Valid options are: friendly, neutral, unfriendly.");
            return true;
        }

        WDEnation senderWdeNation = nationManager.getNation(senderNation.getName());
        if (senderWdeNation != null) {
            senderWdeNation.setDiplomaticRelation(targetNationName, relation);
            sender.sendMessage(ChatColor.GREEN + "Diplomatic relation set to " + relation + " with " + targetNationName);
            nationManager.saveNations();
        } else {
            sender.sendMessage(ChatColor.RED + "Your nation's data could not be found.");
        }

        return true;
    }
    private boolean handleSetTrading(CommandSender sender, String targetNationName, String[] args, JavaPlugin plugin) {
        // Fetch the sender's nation
        Nation senderNation = getNationByResident(sender.getName());
        if (senderNation == null) {
            sender.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
            return true;
        }

        // Check diplomatic authority
        if (!hasDiplomaticAuthority(sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have authority to set trading status.");
            return true;
        }

        boolean tradingEnabled = args[3].equalsIgnoreCase("enabled");
        WDEnation senderWdeNation = nationManager.getNation(senderNation.getName());
        if (senderWdeNation != null) {
            senderWdeNation.setTradingStatus(targetNationName, tradingEnabled);
            sender.sendMessage(ChatColor.GREEN + "Trading status set to " + (tradingEnabled ? "enabled" : "disabled") + " with " + targetNationName);
            nationManager.saveNations();
        } else {
            sender.sendMessage(ChatColor.RED + "Your nation's data could not be found.");
        }

        return true;
    }
    public Nation getNationByResident(String residentName) {
        try {
            // Get the resident object
            Resident resident = TownyUniverse.getInstance().getResident(residentName);
            if (resident != null) {
                // Check if the resident is in a town
                if (resident.hasTown()) {
                    Town town = resident.getTown();
                    // Check if the town is in a nation
                    if (town.hasNation()) {
                        return town.getNation();
                    }
                }
            }
        } catch (NotRegisteredException e) {
            // Handle exception if resident, town, or nation not found
            e.printStackTrace();
        }

        return null; // Return null if no nation is found
    }

    private boolean hasDiplomaticAuthority(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // If the sender is not a player (e.g., console), deny authority
            return false;
        }

        Player player = (Player) sender;
        String senderName = player.getName();

        try {
            // Get the resident object for the sender
            Resident resident = TownyUniverse.getInstance().getResident(senderName);

            // Check if the resident is part of a town and has a nation
            if (resident != null && resident.hasTown() && resident.getTown().hasNation()) {
                Nation senderNation = resident.getTown().getNation();

                // Check if the sender is the NationLeader (king in Towny)
                if (senderName.equalsIgnoreCase(senderNation.getKing().getName())) {
                    return true;
                }

                WDEnation wdeNation = nationManager.getNation(senderNation.getName());
                if (wdeNation != null) {
                    if (senderName.equalsIgnoreCase(wdeNation.getGovernmentLeader()) ||
                            senderName.equalsIgnoreCase(wdeNation.getArmyCommander())) {
                        return true;
                    }
                }
            }
        } catch (NotRegisteredException e) {
            // Handle exception if resident, town, or nation not found
            e.printStackTrace();
        }

        // If none of the above, the sender does not have diplomatic authority
        return false;
    }


    private boolean handleViewTradingRelations(CommandSender sender, String nationName, JavaPlugin plugin) {
        WDEnation nation = nationManager.getNation(nationName);
        if (nation == null) {
            sender.sendMessage(ChatColor.RED + "Nation not found: " + nationName);
            return true;
        }

        Map<String, Boolean> tradingStatus = nation.getTradingStatus();
        if (tradingStatus == null || tradingStatus.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No trading relations set for " + nationName);
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Trading Relations for " + nationName + ":");
        tradingStatus.forEach((otherNation, status) -> {
            String statusText = status ? "Enabled" : "Disabled";
            sender.sendMessage(ChatColor.GOLD + otherNation + ": " + statusText);
        });

        return true;
    }
    private boolean handleViewDiplomaticRelations(CommandSender sender, String nationName, JavaPlugin plugin) {
        WDEnation nation = nationManager.getNation(nationName);
        if (nation == null) {
            sender.sendMessage(ChatColor.RED + "Nation not found: " + nationName);
            return true;
        }

        Map<String, String> diplomaticRelations = nation.getDiplomaticRelations();
        if (diplomaticRelations == null || diplomaticRelations.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No diplomatic relations set for " + nationName);
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Diplomatic Relations for " + nationName + ":");
        diplomaticRelations.forEach((otherNation, relation) -> {
            sender.sendMessage(ChatColor.GOLD + otherNation + ": " + relation);
        });

        return true;
    }


}

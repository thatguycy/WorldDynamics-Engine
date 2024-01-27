package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.thatguycy.worlddynamicsengine.WDEnation;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class NationCommand implements CommandExecutor {

    private NationManager nationManager;

    public NationCommand(NationManager nationManager) {
        this.nationManager = nationManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the nation subcommand and nation name are provided
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nation name>");
            return true;
        }

        // args[0] is 'nation', args[1] should be the nation name
        String nationName = args[1];
        WDEnation nation = nationManager.getNation(nationName);

        // If only the nation name is provided, display nation info
        if (args.length == 2) {
            return displayNationInfo(sender, nationName, nation);
        }

        // Handling cases with additional subcommands
        else if (args.length > 2) {
            switch (args[2].toLowerCase()) {
                case "setgovtype":
                    return handleSetGovType(sender, nation, args, WorldDynamicsEngine.getInstance());
                // Add more cases for other subcommands
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                    return true;
            }
        }

        return true;
    }


    private boolean displayNationInfo(CommandSender sender, String nationName, WDEnation nation) {
        if (nation == null) {
            sender.sendMessage(ChatColor.RED + "Nation not found (or no WDE attributes set): " + nationName);
            return true;
        }

        // Display nation information in a format similar to Towny's /t and /n
        sender.sendMessage(ChatColor.GOLD + "------ Nation Info: " + ChatColor.GREEN + nationName + ChatColor.GOLD + " ------");
        sender.sendMessage(ChatColor.YELLOW + "Government Type: " + ChatColor.WHITE + nation.getGovernmentType());
        sender.sendMessage(ChatColor.YELLOW + "Government Leader: " + ChatColor.WHITE + nation.getGovernmentLeader());

        // Formatting the list of government members
        String membersList = String.join(", ", nation.getGovernmentMembers());
        sender.sendMessage(ChatColor.YELLOW + "Government Members: " + ChatColor.WHITE + (membersList.isEmpty() ? "None" : membersList));

        return true;
    }

    private boolean handleSetGovType(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> setgovtype <type>");
            return true;
        }

        Resident resident = TownyUniverse.getInstance().getResident(sender.getName());
        if (resident == null) {
            sender.sendMessage(ChatColor.RED + "You must be a resident in Towny to perform this action.");
            return true;
        }

        Nation townyNation = TownyUniverse.getInstance().getNation(args[1]);
        if (townyNation == null) {
            sender.sendMessage(ChatColor.RED + "Nation not found in Towny.");
            sender.sendMessage(ChatColor.RED + args[1]);
            return true;
        }

        if (!resident.equals(townyNation.getKing())) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of the nation to perform this action.");
            return true;
        }

        String govType = args[3];
        List<String> validGovTypes = plugin.getConfig().getStringList("validGovernmentTypes");

        // Check if the provided government type is valid
        if (!validGovTypes.contains(govType.toUpperCase())) {
            sender.sendMessage(ChatColor.RED + "Invalid government type. Valid types are: " + String.join(", ", validGovTypes));
            return true;
        }

        // Set the government type
        nation.setGovernmentType(govType);
        sender.sendMessage(ChatColor.GREEN + "Government type set to " + govType + " for " + nation.getNationName());

        // Save the updated nation information
        nationManager.saveNations();

        return true;
    }
}

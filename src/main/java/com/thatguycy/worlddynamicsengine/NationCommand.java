package com.thatguycy.worlddynamicsengine;

import com.thatguycy.worlddynamicsengine.WDEnation;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
                // Example: handle "setgovtype" subcommand
                // case "setgovtype":
                //     return handleSetGovType(sender, nation, args);
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

}

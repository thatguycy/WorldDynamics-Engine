package org.nebulaone.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    // Method to execute when /wde help is used
    public void execute(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "--- " + ChatColor.GOLD + "[ WorldDynamicsEngine Commands ] " + ChatColor.RED + "---");
        sender.sendMessage( ChatColor.DARK_GRAY + "/wde " + ChatColor.GRAY + "help" + ChatColor.DARK_GRAY +  " - " + ChatColor.WHITE + "Show this help message");
        // Add more help info as needed
    }
}
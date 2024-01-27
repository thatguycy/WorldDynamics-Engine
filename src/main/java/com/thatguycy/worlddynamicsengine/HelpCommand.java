package com.thatguycy.worlddynamicsengine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "WorldDynamics Engine Commands:");
        sender.sendMessage(ChatColor.AQUA + "--------[ " + ChatColor.BLUE + "Misc. Commands" + ChatColor.AQUA + " ]--------");
        sender.sendMessage(ChatColor.YELLOW + "/wde help" + ChatColor.WHITE + " - Display this help message.");
        sender.sendMessage(ChatColor.YELLOW + "/wde docs" + ChatColor.WHITE + " - Display a link to the docs.");
        return true;
    }
}
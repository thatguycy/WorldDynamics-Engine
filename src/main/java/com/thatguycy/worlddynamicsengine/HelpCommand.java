package com.thatguycy.worlddynamicsengine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "WorldDynamics Engine Commands:");
        sender.sendMessage(ChatColor.YELLOW + "/wde help" + ChatColor.WHITE + " - Display this help message.");
        return true;
    }
}
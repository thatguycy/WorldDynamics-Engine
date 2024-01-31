package com.thatguycy.worlddynamicsengine;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "WorldDynamics Engine Commands:");

        sender.sendMessage(ChatColor.AQUA + "--------[ " + ChatColor.BLUE + "General Commands" + ChatColor.AQUA + " ]--------");
        sender.sendMessage(ChatColor.YELLOW + "/wde help" + ChatColor.WHITE + " - Display this help message.");
        sender.sendMessage(ChatColor.YELLOW + "/wde docs" + ChatColor.WHITE + " - Display a link to the documentation.");

        sender.sendMessage(ChatColor.AQUA + "--------[ " + ChatColor.BLUE + "Nation Commands" + ChatColor.AQUA + " ]--------");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> setgovtype <type>" + ChatColor.WHITE + " - Set the government type of a nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> setgovleader <username>" + ChatColor.WHITE + " - Set the government leader of a nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> addgovmember <username>" + ChatColor.WHITE + " - Add a government member to a nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> kickgovmember <username>" + ChatColor.WHITE + " - Remove a government member from a nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> appointarmycommander <username>" + ChatColor.WHITE + " - Appoint the army commander of a nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> enlistarmymember <username>" + ChatColor.WHITE + " - Enlist a member to the army.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> dischargearmymember <username>" + ChatColor.WHITE + " - Discharge a member from the army.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> adddiplomat <username>" + ChatColor.WHITE + " - Add a diplomat to a nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde nation <nation> removediplomat <username>" + ChatColor.WHITE + " - Remove a diplomat from a nation.");

        sender.sendMessage(ChatColor.AQUA + "--------[ " + ChatColor.BLUE + "Diplomacy Commands" + ChatColor.AQUA + " ]--------");
        sender.sendMessage(ChatColor.YELLOW + "/wde diplomacy setrelation <nation> <friendly/neutral/unfriendly>" + ChatColor.WHITE + " - Set diplomatic relations with another nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde diplomacy trading <nation> <enabled/disabled>" + ChatColor.WHITE + " - Enable or disable trading with another nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde diplomacy viewtrading <nation>" + ChatColor.WHITE + " - View trading status with another nation.");
        sender.sendMessage(ChatColor.YELLOW + "/wde diplomacy relations <nation>" + ChatColor.WHITE + " - View diplomatic relations with other nations.");
        sender.sendMessage(ChatColor.YELLOW + "/wde diplomacy visit <nation>" + ChatColor.WHITE + " - Visit another nation as a diplomat.");

        return true;
    }
}
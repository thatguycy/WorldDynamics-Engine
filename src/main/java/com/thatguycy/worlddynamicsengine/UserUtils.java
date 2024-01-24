package com.thatguycy.worlddynamicsengine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserUtils {
    // Vanilla Minecraft UserUtils
    public static UUID getPlayersUUID(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        } else {
            return null; // Player not found or not online
        }
    }
    public static String getPlayersUsername(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return player.getName();
        } else {
            return null; // Player not found or not online
        }
    }
    public static List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
    public static Location getPlayerLocation(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            return player.getLocation();
        }
        return null;
    }
    public static boolean handleHelpCommand(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "==== " + ChatColor.AQUA + "WorldDynamics Engine Help" + ChatColor.GOLD + " ====");
        sender.sendMessage(ChatColor.YELLOW + "/wde help " + ChatColor.WHITE + "- Displays this help message.");
        sender.sendMessage(ChatColor.YELLOW + "/wde sub1 " + ChatColor.WHITE + "- Description for subcommand 1.");
        sender.sendMessage(ChatColor.YELLOW + "/wde sub2 " + ChatColor.WHITE + "- Description for subcommand 2.");
        sender.sendMessage(ChatColor.YELLOW + "/wde sub3 " + ChatColor.WHITE + "- Description for subcommand 3.");
        return true; // Indicate that the command was successfully handled
    }
}

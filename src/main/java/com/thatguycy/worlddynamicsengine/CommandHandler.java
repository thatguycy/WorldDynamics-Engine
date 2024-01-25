package com.thatguycy.worlddynamicsengine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {
    private final JavaPlugin plugin;
    private final Map<String, CommandExecutor> subCommandMap;

    public CommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.subCommandMap = new HashMap<>();
        plugin.getCommand("wde").setExecutor(this);
    }

    public void registerSubCommand(String name, CommandExecutor executor) {
        subCommandMap.put(name.toLowerCase(), executor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false; // No subcommand provided
        }

        CommandExecutor subCommand = subCommandMap.get(args[0].toLowerCase());
        if (subCommand != null) {
            return subCommand.onCommand(sender, cmd, label, args);
        }

        sender.sendMessage("Subcommand not found.");
        return true;
    }
}

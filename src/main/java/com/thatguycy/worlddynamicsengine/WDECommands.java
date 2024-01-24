package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.Towny;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WDECommands implements CommandExecutor {

    private final JavaPlugin plugin;

    public WDECommands(JavaPlugin plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        Objects.requireNonNull(plugin.getCommand("wde")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wde")) {
            return handleWDE(sender, args);
        }
        return false;
    }

    private boolean handleWDE(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return UserUtils.handleHelpCommand(sender);
        }

        return switch (args[0].toLowerCase()) {
            case "help" -> UserUtils.handleHelpCommand(sender);
            default -> {
                sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                yield false;
            }
        };
    }
}

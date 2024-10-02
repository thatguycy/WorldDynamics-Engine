package org.nebulaone.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WDECommandExecutor implements CommandExecutor {

    // [thatguycy] Below will be the command subexecutors. ({1/10/2024}/{0.3.0})
    private final HelpCommand helpCommand = new HelpCommand();
    private final ResidentCommand residentCommand = new ResidentCommand(); // Registering the ResidentCommand

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.WHITE + "/wde <subcommand>");
            return false;
        }

        // Handle subcommands based on args[0]
        switch (args[0].toLowerCase()) {
            case "help":
                helpCommand.execute(sender);
                break;
            case "resident": // Handling the 'resident' subcommand
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    residentCommand.execute(player); // Call the resident command
                } else {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + args[0]);
                break;
        }
        return true;
    }
}

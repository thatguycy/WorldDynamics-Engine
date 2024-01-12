package com.thatguycy.worlddynamicsengine;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

public class WDECommandExecutor implements CommandExecutor {

    private NationManager nationManager;

    public WDECommandExecutor(NationManager nationManager) {
        this.nationManager = nationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("wde")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                return displayHelp(sender);
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            Player player = (Player) sender;
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "government":
                    return handleGovernmentCommand(player, args);
                case "nation":
                    if (args.length > 0) {
                        String nationName = args[1];
                        nationManager.displayNationInfo(sender, nationName);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /wde nationinfo <nation>");
                    }
                    return true;
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                    return true;
            }
        }
        return false;
    }

    private boolean displayHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "WorldDynamics Engine Commands:");
        sender.sendMessage(ChatColor.GOLD + "/wde government settype <type> - Set your nation's government type.");
        sender.sendMessage(ChatColor.GOLD + "/wde government info - View your nation's government type.");
        // Add other command descriptions here
        return true;
    }

    private boolean handleGovernmentCommand(Player player, String[] args) {
        try {
            Resident resident = com.palmergames.bukkit.towny.TownyUniverse.getInstance().getResident(player.getName());
            if (!resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /wde government <settype/info>");
                return true;
            }

            if (args[1].equalsIgnoreCase("settype")) {
                if (!resident.isKing()) {
                    player.sendMessage(ChatColor.RED + "You must be the leader of your nation to set the government type.");
                    return true;
                }
                return handleSetGovernmentType(player, nation, args);
            } else if (args[1].equalsIgnoreCase("info")) {
                return handleGovernmentInfo(player, nation);
            } else {
                player.sendMessage(ChatColor.RED + "Unknown subcommand.");
            }

            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Error: You are not part of a nation.");
            return true;
        }
    }

    private boolean handleSetGovernmentType(Player player, Nation nation, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde government settype <type>");
            return true;
        }

        String typeArg = args[2].toUpperCase();
        GovernmentType type;
        try {
            type = GovernmentType.valueOf(typeArg);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid government type: " + typeArg);
            return true;
        }

        NationProperties properties = nationManager.getNationProperties(nation.getName());
        if (properties == null) {
            properties = new NationProperties(type); // Assuming default constructor sets default values
        } else {
            properties.setGovernmentType(type);
        }
        nationManager.setNationProperties(nation.getName(), properties);
        nationManager.saveNations();
        player.sendMessage(ChatColor.GREEN + "Government type set to " + typeArg + " for the nation " + nation.getName());
        return true;
    }

    private boolean handleGovernmentInfo(Player player, Nation nation) {
        NationProperties properties = nationManager.getNationProperties(nation.getName());
        if (properties == null || properties.getGovernmentType() == null) {
            player.sendMessage(ChatColor.RED + "Your nation does not have a set government type.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Your nation's government type is: " + properties.getGovernmentType().name());
        }
        return true;
    }
}


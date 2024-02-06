package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.thatguycy.worlddynamicsengine.WDEnation;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NationCommand implements CommandExecutor {

    private NationManager nationManager;
    private ResidentManager residentManager;

    public NationCommand(NationManager nationManager, ResidentManager residentManager) {
        this.nationManager = nationManager;
        this.residentManager = residentManager;
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
                case "setgovtype":
                    return handleSetGovType(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "setgovleader":
                    return handleSetGovLeader(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "addgovmember":
                    return handleAddGovMember(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "kickgovmember":
                    return handleKickGovMember(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "appointarmycommander":
                    return handleAppointArmyCommander(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "enlistarmymember":
                    return handleEnlistArmyMember(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "dischargearmymember":
                    return handleDischargeArmyMember(sender, nation, args, WorldDynamicsEngine.getInstance());
                case "adddiplomat":
                    if (args.length < 4) {
                        sender.sendMessage(ChatColor.RED + "Usage: /wde nation adddiplomat <username>");
                        return true;
                    }
                    if (!hasNationAuthority(sender, nationManager)) {
                        sender.sendMessage(ChatColor.RED + "You do not have the authority to perform this action.");
                        return true;
                    }
                    return handleAddDiplomat(sender, args[3], residentManager);

                case "removediplomat":
                    if (args.length < 4) {
                        sender.sendMessage(ChatColor.RED + "Usage: /wde nation removediplomat <username>");
                        return true;
                    }
                    if (!hasNationAuthority(sender, nationManager)) {
                        sender.sendMessage(ChatColor.RED + "You do not have the authority to perform this action.");
                        return true;
                    }
                    return handleRemoveDiplomat(sender, args[3], residentManager);
                default:
                    sender.sendMessage(ChatColor.RED + WorldDynamicsEngine.getInstance().getLocaleMessage(WorldDynamicsEngine.getInstance().userLang, "unknownsub"));
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
        sender.sendMessage(ChatColor.GOLD + "========[ " + ChatColor.GREEN + nationName + ChatColor.GOLD + " ]========");
        sender.sendMessage(ChatColor.YELLOW + "Government Type: " + ChatColor.WHITE + nation.getGovernmentType());
        sender.sendMessage(ChatColor.YELLOW + "Government Leader: " + ChatColor.WHITE + nation.getGovernmentLeader());

        // Formatting the list of government members
        String membersList = String.join(", ", nation.getGovernmentMembers());
        sender.sendMessage(ChatColor.YELLOW + "Government Members: " + ChatColor.WHITE + (membersList.isEmpty() ? "None" : membersList));

        sender.sendMessage(ChatColor.YELLOW + "Army Commander: " + ChatColor.WHITE + nation.getArmyLeader());

        // Formatting the list of government members
        String armyMembersList = String.join(", ", nation.getArmyMembers());
        sender.sendMessage(ChatColor.YELLOW + "Army Members: " + ChatColor.WHITE + (armyMembersList.isEmpty() ? "None" : armyMembersList));

        return true;
    }

    private boolean handleSetGovType(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> setgovtype <type>");
            return true;
        }

        Resident resident = TownyUniverse.getInstance().getResident(sender.getName());
        if (resident == null) {
            sender.sendMessage(ChatColor.RED + "You must be a resident in Towny to perform this action.");
            return true;
        }

        Nation townyNation = TownyUniverse.getInstance().getNation(args[1]);
        if (townyNation == null) {
            sender.sendMessage(ChatColor.RED + "Nation not found in Towny.");
            sender.sendMessage(ChatColor.RED + args[1]);
            return true;
        }

        if (!resident.equals(townyNation.getKing())) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of the nation to perform this action.");
            return true;
        }

        String govType = args[3];
        List<String> validGovTypes = plugin.getConfig().getStringList("validGovernmentTypes");

        // Check if the provided government type is valid
        if (!validGovTypes.contains(govType.toUpperCase())) {
            sender.sendMessage(ChatColor.RED + "Invalid government type. Valid types are: " + String.join(", ", validGovTypes));
            return true;
        }

        // Set the government type
        nation.setGovernmentType(govType);
        sender.sendMessage(ChatColor.GREEN + "Government type set to " + govType + " for " + nation.getNationName());

        // Save the updated nation information
        nationManager.saveNations();

        return true;
    }

    private boolean handleSetGovLeader(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> setgovleader <playerName>");
            return true;
        }

        // Check if the sender is the Towny Nation King
        Resident resident = TownyUniverse.getInstance().getResident(sender.getName());
        Nation townyNation = TownyUniverse.getInstance().getNation(nation.getNationName());
        if (townyNation == null || resident == null || !resident.equals(townyNation.getKing())) {
            sender.sendMessage(ChatColor.RED + "You must be the king of the nation to perform this action.");
            return true;
        }

        String newLeaderName = args[3];
        // Additional checks if needed and then set the government leader
        nation.setGovernmentLeader(newLeaderName);
        sender.sendMessage(ChatColor.GREEN + "Government leader set to " + newLeaderName + " for " + nation.getNationName());

        nationManager.saveNations();
        return true;
    }

    private boolean handleAddGovMember(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> addgovmember <playerName>");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase(nation.getGovernmentLeader())) {
            sender.sendMessage(ChatColor.RED + "You must be the government leader to perform this action.");
            return true;
        }

        String memberName = args[3];
        List<String> members = nation.getGovernmentMembers();
        if (!members.contains(memberName)) {
            members.add(memberName);
            nation.setGovernmentMembers(members);
            sender.sendMessage(ChatColor.GREEN + "Member " + memberName + " added to government.");
        } else {
            sender.sendMessage(ChatColor.RED + "Member is already in the government.");
        }

        nationManager.saveNations();
        return true;
    }


    private boolean handleKickGovMember(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> kickgovmember <playerName>");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase(nation.getGovernmentLeader())) {
            sender.sendMessage(ChatColor.RED + "You must be the government leader to perform this action.");
            return true;
        }

        String memberName = args[3];
        List<String> members = nation.getGovernmentMembers();
        if (members.contains(memberName)) {
            members.remove(memberName);
            nation.setGovernmentMembers(members);
            sender.sendMessage(ChatColor.GREEN + "Member " + memberName + " removed from government.");
        } else {
            sender.sendMessage(ChatColor.RED + "Member is not in the government.");
        }

        nationManager.saveNations();
        return true;
    }


    private boolean handleAppointArmyCommander(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> appointarmycommander <playerName>");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase(nation.getGovernmentLeader())) {
            sender.sendMessage(ChatColor.RED + "You must be the government leader to perform this action.");
            return true;
        }

        String commanderName = args[3];
        nation.setArmyCommander(commanderName);
        sender.sendMessage(ChatColor.GREEN + "Army commander appointed: " + commanderName);

        nationManager.saveNations();
        return true;
    }

    private boolean handleEnlistArmyMember(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> enlistarmymember <playerName>");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase(nation.getArmyCommander())) {
            sender.sendMessage(ChatColor.RED + "You must be the army commander to perform this action.");
            return true;
        }

        String memberName = args[3];
        List<String> armyMembers = nation.getArmyMembers();
        if (!armyMembers.contains(memberName)) {
            armyMembers.add(memberName);
            nation.setArmyMembers(armyMembers);
            sender.sendMessage(ChatColor.GREEN + "Member " + memberName + " enlisted in the army.");
        } else {
            sender.sendMessage(ChatColor.RED + "Member is already enlisted in the army.");
        }

        nationManager.saveNations();
        return true;
    }


    private boolean handleDischargeArmyMember(CommandSender sender, WDEnation nation, String[] args, JavaPlugin plugin) {
        if (nation == null || args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde nation <nationname> dischargearmymember <playerName>");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase(nation.getArmyCommander())) {
            sender.sendMessage(ChatColor.RED + "You must be the army commander to perform this action.");
            return true;
        }

        String memberName = args[3];
        List<String> armyMembers = nation.getArmyMembers();
        if (armyMembers.contains(memberName)) {
            armyMembers.remove(memberName);
            nation.setArmyMembers(armyMembers);
            sender.sendMessage(ChatColor.GREEN + "Member " + memberName + " discharged from the army.");
        } else {
            sender.sendMessage(ChatColor.RED + "Member is not enlisted in the army.");
        }

        nationManager.saveNations();
        return true;
    }

    private boolean handleAddDiplomat(CommandSender sender, String username, ResidentManager residentManager) {
        Player targetPlayer = WorldDynamicsEngine.getInstance().getServer().getPlayer(username);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        UUID uuid = targetPlayer.getUniqueId();
        WDEresident resident = residentManager.getResident(uuid);
        if (resident == null) {
            resident = new WDEresident(uuid, username); // Create a new resident if not found
            residentManager.addResident(resident);
        }

        resident.addFlag("diplomat");
        residentManager.saveResidents(); // Save changes
        sender.sendMessage(ChatColor.GREEN + "Diplomat status added to " + username);
        return true;
    }


    private boolean handleRemoveDiplomat(CommandSender sender, String username, ResidentManager residentManager) {
        Player targetPlayer = WorldDynamicsEngine.getInstance().getServer().getPlayer(username);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        UUID uuid = targetPlayer.getUniqueId();
        WDEresident resident = residentManager.getResident(uuid);
        if (resident == null) {
            sender.sendMessage(ChatColor.RED + "Resident data not found for " + username);
            return true;
        }

        resident.removeFlag("diplomat");
        residentManager.saveResidents(); // Save changes
        sender.sendMessage(ChatColor.GREEN + "Diplomat status removed from " + username);
        return true;
    }

    private boolean hasNationAuthority(CommandSender sender, NationManager nationManager) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasTown()) {
                return false;
            }

            Town town = resident.getTown();
            if (town.hasNation()) {
                Nation nation = town.getNation();
                WDEnation wdeNation = nationManager.getNation(nation.getName());

                // Check if player is the Nation Leader in Towny
                if (nation.isKing(resident)) {
                    return true;
                }

                // Check if player is the Government Leader or Army Leader in WDEnation
                String playerName = player.getName();
                if (wdeNation != null) {
                    if (wdeNation.getGovernmentLeader().equalsIgnoreCase(playerName) ||
                            wdeNation.getArmyLeader().equalsIgnoreCase(playerName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}

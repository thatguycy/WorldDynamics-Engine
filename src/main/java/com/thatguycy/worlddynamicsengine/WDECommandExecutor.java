package com.thatguycy.worlddynamicsengine;
import net.milkbowl.vault.economy.Economy;
import com.palmergames.bukkit.towny.TownyUniverse;
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
    private OrganizationManager organizationManager;
    private Economy economy;

    public WDECommandExecutor(NationManager nationManager, OrganizationManager organizationManager, Economy economy) {
        this.nationManager = nationManager;
        this.organizationManager = organizationManager;
        this.economy = economy;
    }

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
            String subCommand = args.length > 0 ? args[0].toLowerCase() : "";

            switch (subCommand) {
                case "org":
                    return handleOrgCommand(player, args);
                case "army":
                    return handleArmyCommand(player, args);
                case "government":
                    return handleGovernmentCommand(player, args);
                case "nation":
                    if (args.length > 1) {
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

    private boolean handleOrgCommand(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org <create>");
            return true;
        }

        String orgCommand = args[1].toLowerCase();
        switch (orgCommand) {
            case "create":
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Usage: /wde org create <name> <BUSINESS/INTERNATIONAL>");
                    return true;
                }
                return handleCreateOrganization(player, args);
            // Add other organization subcommands here
            default:
                player.sendMessage(ChatColor.RED + "Unknown organization subcommand.");
                return true;
        }
    }

    private boolean displayHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "WorldDynamics Engine Commands:");
        sender.sendMessage(ChatColor.GOLD + "/wde government settype <type> - Set your nation's government type.");
        sender.sendMessage(ChatColor.GOLD + "/wde government info - View your nation's government type.");

        // Army related commands
        sender.sendMessage(ChatColor.GOLD + "/wde army setleader <player> - Set the leader of your nation's army.");
        sender.sendMessage(ChatColor.GOLD + "/wde army addmember <player> - Add a member to your nation's army.");
        sender.sendMessage(ChatColor.GOLD + "/wde army kickmember <player> - Remove a member from your nation's army.");
        sender.sendMessage(ChatColor.GOLD + "/wde army leave - Leave your nation's army.");

        // Government related commands
        sender.sendMessage(ChatColor.GOLD + "/wde government setleader <player> - Set the leader of your nation's government.");
        sender.sendMessage(ChatColor.GOLD + "/wde government addmember <player> - Add a member to your nation's government.");
        sender.sendMessage(ChatColor.GOLD + "/wde government kickmember <player> - Remove a member from your nation's government.");
        sender.sendMessage(ChatColor.GOLD + "/wde government leave - Leave your nation's government.");
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
            NationProperties properties = nationManager.getNationProperties(nation.getName());
            if (properties == null) {
                player.sendMessage(ChatColor.RED + "Your nation does not have any attributes assigned by WDE.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /wde government <settype|info|setleader|addmember|kickmember|leave>");
                return true;
            }

            String govCommand = args[1].toLowerCase();

            if (govCommand.equals("settype")) {
                if (!resident.isKing()) {
                    player.sendMessage(ChatColor.RED + "You must be the leader of your nation to set the government type.");
                    return true;
                }
                return handleSetGovernmentType(player, nation, args);
            } else if (govCommand.equals("info")) {
                return handleGovernmentInfo(player, nation);
            } else if (govCommand.equals("setleader")) {
                return handleSetGovLeader(player, nation, properties, args);
            } else if (govCommand.equals("addmember")) {
                return handleAddGovMember(player, args);
            } else if (govCommand.equals("kickmember")) {
                return handleKickGovMember(player, args);
            } else if (govCommand.equals("leave")) {
                return handleGovMemberLeave(player);
            } else {
                player.sendMessage(ChatColor.RED + "Unknown subcommand.");
                return true;
            }
        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Error: Nation not found.");
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

    private boolean handleArmyCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /wde army <setleader|addmember|kickmember|leave>");
            return true;
        }

        String armyCommand = args[1].toLowerCase();
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null || !resident.hasNation()) {
            player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
            return true;
        }

        try {
            Nation nation = resident.getTown().getNation();
            NationProperties properties = nationManager.getNationProperties(nation.getName());
            if (properties == null) {
                player.sendMessage(ChatColor.RED + "Your nation must have a set government type.");
                return true;
            }

            switch (armyCommand) {
                case "setleader":
                    return handleSetArmyLeader(player, nation, properties, args);
                case "addmember":
                    return handleAddArmyMember(player, args);
                case "kickmember":
                    return handleKickArmyMember(player, args);
                case "leave":
                    return handleArmyMemberLeave(player);
                default:
                    player.sendMessage(ChatColor.RED + "Unknown army command.");
                    return true;
            }
        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Error: Nation not found.");
            return true;
        }
    }

    private boolean handleAddArmyMember(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde army addmember <player>");
            return true;
        }

        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            if (!isArmyLeader(player, nation)) {
                player.sendMessage(ChatColor.RED + "Only the army leader can add members to the army.");
                return true;
            }

            String newMemberName = args[2];
            Resident newMember = TownyUniverse.getInstance().getResident(newMemberName);
            if (newMember == null || !newMember.hasTown() || !newMember.getTown().getNation().equals(nation)) {
                player.sendMessage(ChatColor.RED + "Specified player is not a member of your nation.");
                return true;
            }

            NationProperties properties = nationManager.getNationProperties(nation.getName());
            properties.addArmyMember(newMemberName);
            nationManager.saveNations();
            player.sendMessage(ChatColor.GREEN + newMemberName + " has been added to the army.");
            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Nation or town not found.");
            return true;
        }
    }

    private boolean handleKickArmyMember(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde army kickmember <player>");
            return true;
        }

        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            if (!isArmyLeader(player, nation)) {
                player.sendMessage(ChatColor.RED + "Only the army leader can kick members from the army.");
                return true;
            }

            String memberName = args[2];
            NationProperties properties = nationManager.getNationProperties(nation.getName());
            if (!properties.getArmyMembers().contains(memberName)) {
                player.sendMessage(ChatColor.RED + "Specified player is not a member of the army.");
                return true;
            }

            properties.removeArmyMember(memberName);
            nationManager.saveNations();
            player.sendMessage(ChatColor.GREEN + memberName + " has been removed from the army.");
            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Nation or town not found.");
            return true;
        }
    }
    private boolean handleArmyMemberLeave(Player player) {
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            NationProperties properties = nationManager.getNationProperties(nation.getName());
            if (properties == null || !properties.getArmyMembers().contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "You are not a member of any army.");
                return true;
            }

            properties.removeArmyMember(player.getName());
            nationManager.saveNations();
            player.sendMessage(ChatColor.GREEN + "You have successfully left the army of " + nation.getName() + ".");
            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Nation or town not found.");
            return true;
        }
    }


    private boolean handleSetArmyLeader(Player player, Nation nation, NationProperties properties, String[] args) {
        if (!isNationLeader(player, nation)) {
            player.sendMessage(ChatColor.RED + "Only the nation leader can set the army leader.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde army setleader <player>");
            return true;
        }

        String leaderName = args[2];
        Resident newLeader = TownyUniverse.getInstance().getResident(leaderName);
        if (newLeader == null) {
            player.sendMessage(ChatColor.RED + "Player '" + leaderName + "' does not exist.");
            return true;
        }

        try {
            if (!newLeader.hasTown() || !newLeader.getTown().getNation().equals(nation)) {
                player.sendMessage(ChatColor.RED + "Specified player is not a member of your nation.");
                return true;
            }
        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Specified player is not part of any town.");
            return true;
        }

        properties.setArmyLeader(newLeader);
        nationManager.saveNations();
        player.sendMessage(ChatColor.GREEN + "Army leader set to " + leaderName);
        return true;
    }

    private boolean isNationLeader(Player player, Nation nation) {
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                return false;
            }

            Nation playerNation = resident.getTown().getNation();
            return playerNation.equals(nation) && resident.isKing();
        } catch (NotRegisteredException e) {
            return false;
        }
    }
    private boolean isArmyLeader(Player player, Nation nation) {
        NationProperties properties = nationManager.getNationProperties(nation.getName());
        if (properties == null || properties.getArmyLeader() == null) {
            return false;
        }

        return properties.getArmyLeader().getName().equalsIgnoreCase(player.getName());
    }
    private boolean isGovernmentLeader(Player player, Nation nation) {
        NationProperties properties = nationManager.getNationProperties(nation.getName());
        if (properties == null || properties.getGovernmentLeader() == null) {
            return false;
        }

        return properties.getGovernmentLeader().getName().equalsIgnoreCase(player.getName());
    }
    private boolean handleAddGovMember(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde government addmember <player>");
            return true;
        }

        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            if (!isGovernmentLeader(player, nation)) {
                player.sendMessage(ChatColor.RED + "Only the government leader can add members to the government.");
                return true;
            }

            String newMemberName = args[2];
            Resident newMember = TownyUniverse.getInstance().getResident(newMemberName);
            if (newMember == null || !newMember.hasTown() || !newMember.getTown().getNation().equals(nation)) {
                player.sendMessage(ChatColor.RED + "Specified player is not a member of your nation.");
                return true;
            }

            NationProperties properties = nationManager.getNationProperties(nation.getName());
            properties.addGovernmentMember(newMemberName);
            nationManager.saveNations();
            player.sendMessage(ChatColor.GREEN + newMemberName + " has been added to the government.");
            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Nation or town not found.");
            return true;
        }
    }


    private boolean handleKickGovMember(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde government kickmember <player>");
            return true;
        }

        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            if (!isGovernmentLeader(player, nation)) {
                player.sendMessage(ChatColor.RED + "Only the government leader can kick members from the government.");
                return true;
            }

            String memberName = args[2];
            NationProperties properties = nationManager.getNationProperties(nation.getName());
            if (!properties.getGovernmentMembers().contains(memberName)) {
                player.sendMessage(ChatColor.RED + "Specified player is not a member of the government.");
                return true;
            }

            properties.removeGovernmentMember(memberName);
            nationManager.saveNations();
            player.sendMessage(ChatColor.GREEN + memberName + " has been removed from the government.");
            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Nation or town not found.");
            return true;
        }
    }

    private boolean handleGovMemberLeave(Player player) {
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident == null || !resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            NationProperties properties = nationManager.getNationProperties(nation.getName());
            if (properties == null || !properties.getGovernmentMembers().contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "You are not a member of the government.");
                return true;
            }

            properties.removeGovernmentMember(player.getName());
            nationManager.saveNations();
            player.sendMessage(ChatColor.GREEN + "You have successfully left the government of " + nation.getName() + ".");
            return true;

        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Nation or town not found.");
            return true;
        }
    }



    private boolean handleSetGovLeader(Player player, Nation nation, NationProperties properties, String[] args) {
        if (!isNationLeader(player, nation)) {
            player.sendMessage(ChatColor.RED + "Only the nation leader can set the government leader.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde government setleader <player>");
            return true;
        }

        String leaderName = args[2];
        Resident newLeader = TownyUniverse.getInstance().getResident(leaderName);
        if (newLeader == null) {
            player.sendMessage(ChatColor.RED + "Player '" + leaderName + "' does not exist.");
            return true;
        }

        try {
            if (!newLeader.hasTown() || !newLeader.getTown().getNation().equals(nation)) {
                player.sendMessage(ChatColor.RED + "Specified player is not a member of your nation.");
                return true;
            }
        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "Specified player is not part of any town.");
            return true;
        }

        properties.setGovernmentLeader(newLeader);
        nationManager.saveNations();
        player.sendMessage(ChatColor.GREEN + "Government leader set to " + leaderName);
        return true;
    }

    private boolean handleCreateOrganization(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org create <name> <type>");
            return true;
        }

        String orgName = args[2];
        String orgTypeStr = args[3].toUpperCase();
        double cost = 50000.0;

        // Check if the organization type is valid
        OrganizationProperties.OrganizationType orgType;
        try {
            orgType = OrganizationProperties.OrganizationType.valueOf(orgTypeStr);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid organization type. Please choose BUSINESS or INTERNATIONAL.");
            return true;
        }

        // Check for INTERNATIONAL creation by non-nation leaders
        if (orgType == OrganizationProperties.OrganizationType.INTERNATIONAL && !isAnyNationLeader(player)) {
            player.sendMessage(ChatColor.RED + "Only nation leaders can create INTERNATIONAL organizations.");
            return true;
        }

        // Check if player has enough money
        if (economy.getBalance(player) < cost) {
            player.sendMessage(ChatColor.RED + "You need $" + cost + " to create an organization.");
            return true;
        }

        // Deduct money and create organization
        economy.withdrawPlayer(player, cost);
        OrganizationProperties orgProps = new OrganizationProperties(orgName, player.getName(), orgType);
        organizationManager.addOrganization(orgName, orgProps);
        organizationManager.saveOrganizations(); // Save organizations data

        player.sendMessage(ChatColor.GREEN + "Organization '" + orgName + "' of type '" + orgTypeStr + "' created successfully!");
        return true;
    }
    private boolean isAnyNationLeader(Player player) {
        // Get the Resident object for the player
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) {
            return false;
        }

        // Check if the resident is a leader of any nation
        try {
            return resident.hasNation() && resident.isKing();
        } catch (Exception e) {
            // Handle potential exceptions here, possibly logging or sending an error message to the player
            return false;
        }
    }
}


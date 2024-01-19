package com.thatguycy.worlddynamicsengine;
import com.palmergames.bukkit.towny.object.Town;
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
    private WorldDynamicsEngine main;

    public WDECommandExecutor(NationManager nationManager, OrganizationManager organizationManager, Economy economy, WorldDynamicsEngine main) {
        this.nationManager = nationManager;
        this.organizationManager = organizationManager;
        this.economy = economy;
        this.main = main;
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
                        sender.sendMessage(ChatColor.RED + "Usage: /wde nation info <nation>");
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
        if (!(main.isOrganizationsEnabled())) {
            player.sendMessage(ChatColor.RED + "Organizations are not enabled!");
            return true;
        }
        String orgCommand = args[1].toLowerCase();
        switch (orgCommand) {
            case "create":
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "Usage: /wde org create <name> <BUSINESS/INTERNATIONAL/GOVERNMENTAL>");
                    return true;
                }
                return handleCreateOrganization(player, args);
            case "setattr":
                return handleSetAttribute(player, args);
            case "info":
                return handleOrgInfo(player, args);
            case "deposit":
                return handleOrgDeposit(player, args);
            case "withdraw":
                return handleOrgWithdraw(player, args);
            case "join":
                return handleOrgJoin(player, args);
            case "leave":
                return handleOrgLeave(player, args);
            case "addmember":
                return handleOrgAddMember(player, args);
            case "kickmember":
                return handleOrgKickMember(player, args);
            default:
                player.sendMessage(ChatColor.RED + "Unknown organization subcommand.");
                return true;
        }
    }

    private boolean displayHelp(CommandSender sender) {
        StringBuilder helpMessageBuilder = new StringBuilder();
        helpMessageBuilder.append(ChatColor.YELLOW).append("WorldDynamics Engine Commands:\n");
        helpMessageBuilder.append(ChatColor.BLUE).append("----------------[ Gov Commands ]----------------\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " government settype <type> -" + ChatColor.WHITE + " Set your nation's government type.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " government info -" + ChatColor.WHITE + " View your nation's government type.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " government setleader <player> -" + ChatColor.WHITE + " Set the leader of your nation's government.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " government addmember <player> -" + ChatColor.WHITE + " Add a member to your nation's government.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " government kickmember <player> -" + ChatColor.WHITE + " Remove a member from your nation's government.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " government leave -" + ChatColor.WHITE + " Leave your nation's government.\n");
        helpMessageBuilder.append(ChatColor.BLUE).append("----------------[ Army Commands ]----------------");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " army setleader <player> -" + ChatColor.WHITE + " Set the leader of your nation's army.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " army addmember <player> -" + ChatColor.WHITE + " Add a member to your nation's army.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " army kickmember <player> -" + ChatColor.WHITE + " Remove a member from your nation's army.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " army leave -" + ChatColor.WHITE + " Leave your nation's army.\n");
        helpMessageBuilder.append(ChatColor.BLUE).append("----------------[ Org Commands ]----------------\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org create <type> <name> -" + ChatColor.WHITE + " Create a new organization.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org deposit <orgname> <amount> -" + ChatColor.WHITE + " Deposit money into an organization.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org withdraw <orgname> <amount> -" + ChatColor.WHITE + " Withdraw money from an organization.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org join <orgname> -" + ChatColor.WHITE + " Join an organization.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org leave <orgname> -" + ChatColor.WHITE + " Leave an organization.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org addmember <orgname> <user> -" + ChatColor.WHITE + " Add a member to a GOVERNMENTAL organization (OrgLeader only).\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org kickmember <orgname> <user> -" + ChatColor.WHITE + " Remove a member from an organization (OrgLeader only).\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org info <orgname> -" + ChatColor.WHITE + " Display information about an organization.\n");
        helpMessageBuilder.append(ChatColor.GOLD + "/wde" + ChatColor.YELLOW + " org setattr <orgname> <attribute> -" + ChatColor.WHITE +  "Set an organization's attribute.\n");

        sender.sendMessage(helpMessageBuilder.toString());
        return true;
    }

    private boolean handleGovernmentCommand(Player player, String[] args) {
        if (!(main.isGovernmentEnabled())) {
            player.sendMessage(ChatColor.RED + "Governments are not enabled!");
            return true;
        }
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (!resident.hasNation()) {
                player.sendMessage(ChatColor.RED + "You must be part of a nation to use this command.");
                return true;
            }

            Nation nation = resident.getTown().getNation();
            NationProperties properties = nationManager.getNationProperties(nation.getName());

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
        String type = null;
        try {
            if (GovernmentType.isValidType(typeArg)) {
                type = typeArg;
            }
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
            player.sendMessage(ChatColor.GREEN + "Your nation's government type is: " + properties.getGovernmentType());
        }
        return true;
    }

    private boolean handleArmyCommand(Player player, String[] args) {
        if (!(main.isArmyEnabled())) {
            player.sendMessage(ChatColor.RED + "Armies are not enabled!");
            return true;
        }
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

        if (orgType == OrganizationProperties.OrganizationType.GOVERNMENTAL && !isAnyNationLeader(player)) {
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

    private boolean handleCheckOrgBalance(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org balance <orgName>");
            return true;
        }

        String orgName = args[1];
        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);
        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization not found.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "The balance of " + orgName + " is $" + orgProps.getBalance());
        return true;
    }

    private boolean handleOrgInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde org info <orgName>");
            return true;
        }

        String orgName = args[2];
        displayOrgInfo(sender, orgName);
        return true;
    }

    public void displayOrgInfo(CommandSender sender, String orgName) {
        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);

        // Header
        String header = ChatColor.GOLD + "---------------=[" + ChatColor.GREEN + " " + orgName + " " + ChatColor.GOLD + "]=---------------";
        sender.sendMessage(header);

        if (orgProps != null) {
            // Display organization info
            String orgType = orgProps.getType() != null ? orgProps.getType().name() : "Unknown";
            sender.sendMessage(ChatColor.YELLOW + "Organization Type: " + ChatColor.WHITE + orgType);
            String leaderName = orgProps.getLeader() != null ? orgProps.getLeader() : "None";
            sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.WHITE + leaderName);
            String members = String.join(", ", orgProps.getMembers());
            sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + (members.isEmpty() ? "None" : members));
            String balance = String.format("$%.2f", orgProps.getBalance());
            sender.sendMessage(ChatColor.YELLOW + "Balance: " + ChatColor.WHITE + balance);
            String orgAttr = orgProps.getType() != null ? orgProps.getAttribute().name() : "None";
            sender.sendMessage(ChatColor.YELLOW + "Organization Attribute: " + ChatColor.WHITE + orgAttr);

        } else {
            // Organization does not exist
            sender.sendMessage(ChatColor.RED + "Organization does not exist.");
        }
    }
    private boolean handleOrgDeposit(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org deposit <orgName> <amount>");
            return true;
        }

        String orgName = args[2];
        double amount;

        try {
            amount = Double.parseDouble(args[3]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "Please enter a positive amount.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            return true;
        }

        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);
        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        // Check if the player is a member or leader of the organization
        if (!orgProps.getLeader().equalsIgnoreCase(player.getName()) && !orgProps.getMembers().contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You must be a leader or a member of the organization to deposit funds.");
            return true;
        }

        // Check if the player has enough money
        if (economy.getBalance(player) < amount) {
            player.sendMessage(ChatColor.RED + "You do not have enough funds to deposit. Your balance: $" + economy.getBalance(player));
            return true;
        }

        // Perform the deposit
        economy.withdrawPlayer(player, amount);
        orgProps.deposit(amount);
        organizationManager.saveOrganizations(); // Save the organization data

        player.sendMessage(ChatColor.GREEN + "Deposited $" + amount + " to the organization: " + orgName);
        return true;
    }
    private boolean handleOrgWithdraw(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org withdraw <orgName> <amount>");
            return true;
        }

        String orgName = args[2];
        double amount;

        try {
            amount = Double.parseDouble(args[3]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "Please enter a positive amount.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount. Please enter a valid number.");
            return true;
        }

        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);
        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        // Check if the player is a member or leader of the organization
        if (!orgProps.getLeader().equalsIgnoreCase(player.getName()) && !orgProps.getMembers().contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You must be a leader or a member of the organization to withdraw funds.");
            return true;
        }

        // Check if the organization has enough money
        if (orgProps.getBalance() < amount) {
            player.sendMessage(ChatColor.RED + "The organization does not have enough funds to withdraw. Current balance: $" + orgProps.getBalance());
            return true;
        }

        // Perform the withdrawal
        orgProps.withdraw(amount);
        economy.depositPlayer(player, amount);
        organizationManager.saveOrganizations(); // Save the organization data

        player.sendMessage(ChatColor.GREEN + "Withdrew $" + amount + " from the organization: " + orgName);
        return true;
    }
    private boolean handleOrgJoin(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org join <orgName>");
            return true;
        }

        String orgName = args[2];
        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);

        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        if (orgProps.getMembers().contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You are already a member of this organization.");
            return true;
        }

        if (orgProps.getType() == OrganizationProperties.OrganizationType.INTERNATIONAL && !isAnyNationLeader(player)) {
            player.sendMessage(ChatColor.RED + "Only nation leaders can join INTERNATIONAL organizations.");
            return true;
        }

        if (orgProps.getType() == OrganizationProperties.OrganizationType.GOVERNMENTAL && !isAnyNationLeader(player)) {
            player.sendMessage(ChatColor.RED + "You can't join a Governmental Organization!");
            return true;
        }


        // Add the player to the organization
        orgProps.addMember(player.getName());
        organizationManager.saveOrganizations();
        player.sendMessage(ChatColor.GREEN + "You have successfully joined the organization: " + orgName);
        return true;
    }

    private boolean handleOrgLeave(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org leave <orgName>");
            return true;
        }

        String orgName = args[2];
        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);

        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        if (!orgProps.getMembers().contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You are not a member of this organization.");
            return true;
        }

        // Remove the player from the organization's members list
        orgProps.getMembers().remove(player.getName());
        organizationManager.saveOrganizations();
        player.sendMessage(ChatColor.GREEN + "You have successfully left the organization: " + orgName);
        return true;
    }
    private boolean handleOrgAddMember(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org addmember <orgName> <user>");
            return true;
        }

        String orgName = args[2];
        String userName = args[3];
        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);

        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        if (!orgProps.getLeader().equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "Only the organization leader can add members.");
            return true;
        }

        if (orgProps.getType() != OrganizationProperties.OrganizationType.GOVERNMENTAL) {
            player.sendMessage(ChatColor.RED + "This command can only be used for GOVERNMENTAL organizations.");
            return true;
        }

        if (orgProps.getMembers().contains(userName)) {
            player.sendMessage(ChatColor.RED + "User '" + userName + "' is already a member of this organization.");
            return true;
        }

        orgProps.addMember(userName);
        organizationManager.saveOrganizations();
        player.sendMessage(ChatColor.GREEN + "User '" + userName + "' has been added to the organization: " + orgName);
        return true;
    }
    private boolean handleOrgKickMember(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "Usage: /wde org kickmember <orgName> <user>");
            return true;
        }

        String orgName = args[2];
        String userName = args[3];
        OrganizationProperties orgProps = organizationManager.getOrganization(orgName);

        if (orgProps == null) {
            player.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        if (!orgProps.getLeader().equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "Only the organization leader can kick members.");
            return true;
        }

        if (!orgProps.getMembers().contains(userName)) {
            player.sendMessage(ChatColor.RED + "User '" + userName + "' is not a member of this organization.");
            return true;
        }

        orgProps.getMembers().remove(userName);
        organizationManager.saveOrganizations();
        player.sendMessage(ChatColor.GREEN + "User '" + userName + "' has been removed from the organization: " + orgName);
        return true;
    }
    private boolean handleSetAttribute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde org setattr <orgName> <attribute>");
            return true;
        }

        String orgName = args[2];
        String attributeName = args[3].toUpperCase();

        // Retrieve the organization
        OrganizationProperties org = organizationManager.getOrganization(orgName);
        if (org == null) {
            sender.sendMessage(ChatColor.RED + "Organization '" + orgName + "' not found.");
            return true;
        }

        // Get the nation this organization belongs to
        Nation orgNation = getNationForOrganization(org); // Implement this method
        if (orgNation == null) {
            sender.sendMessage(ChatColor.RED + "The organization '" + orgName + "' does not belong to any nation.");
            return true;
        }

        // Check if the sender is the leader of the nation
        if (!isNationLeader(player, orgNation)) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of the nation to set attributes for its organizations.");
            return true;
        }

        // Check if the attribute is valid and set it
        try {
            OrganizationProperties.OrganizationAttribute attribute = OrganizationProperties.OrganizationAttribute.valueOf(attributeName);
            org.setAttribute(attribute);
            organizationManager.saveOrganizations(); // Save the organization data
            sender.sendMessage(ChatColor.GREEN + "Attribute '" + attribute + "' set successfully for organization '" + orgName + "'.");
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid attribute. Valid attributes are: NONE, BANK, PASSPORT_OFFICE, ...");
        }

        return true;
    }

    private Nation getNationForOrganization(OrganizationProperties org) {
        try {
            // Get the leader of the organization
            String leaderName = org.getLeader();

            // Use Towny API to get the Resident object for the leader
            Resident leaderResident = TownyUniverse.getInstance().getResident(leaderName);
            if (leaderResident == null || !leaderResident.hasTown()) {
                return null; // Leader is not part of any town
            }

            // Get the town of the resident
            Town town = leaderResident.getTown();

            // Check if the town is part of a nation
            if (!town.hasNation()) {
                return null; // The town is not part of a nation
            }

            // Return the nation
            return town.getNation();

        } catch (NotRegisteredException e) {
            // This exception is thrown if the resident doesn't exist or isn't part of a town/nation
            return null;
        }
    }

}


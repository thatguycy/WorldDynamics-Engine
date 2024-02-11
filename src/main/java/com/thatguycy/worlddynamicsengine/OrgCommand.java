package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.TownyComponents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OrgCommand implements CommandExecutor {

    private NationManager nationManager;
    private ResidentManager residentManager;
    private OrgManager orgManager;
    private Economy economy;

    public OrgCommand(NationManager nationManager, ResidentManager residentManager, OrgManager orgManager, Economy economy) {
        this.nationManager = nationManager;
        this.residentManager = residentManager;
        this.orgManager = orgManager;
        this.economy = economy;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Safety Check – Minimum Length
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /wde org <org_name>");
            return true;
        }

        String orgName = args[1];
        WDEorg org = orgManager.getOrganization(orgName);

        // Handle 'info' as an Explicit Subcommand
        if (args.length >= 3 && args[2].equalsIgnoreCase("info")) {
            return displayOrgInfo(sender, orgName, org);
        }

        // Other Subcommands (Ensure args-length checks are included within each!)
        if (args.length >= 3) {
            String subcommand = args[2].toLowerCase();

            if (subcommand.equals("create")) {
                if (WorldDynamicsEngine.getInstance().orgEnabled) {
                    return handleCreateOrg(sender, org, args, WorldDynamicsEngine.getInstance());
                } else {
                    sender.sendMessage(ChatColor.RED + "Organizations are not enabled.");
                }
            } else if (subcommand.equals("dissolve")) {
                return handleDissolve(sender, org, args);
            } else if (subcommand.equals("addmember")) {
                return handleAddMember(sender, org, args);
            } else if (subcommand.equals("kickmember")) {
                return handleKickMember(sender, org, args);
            } else if (subcommand.equals("deposit")) {
                return handleDeposit(sender, org, args);
            } else if (subcommand.equals("withdraw")) {
                return handleWithdraw(sender, org, args);
            } else if (subcommand.equals("leave")) {
                return handleLeave(sender, org, args);
            } else if (subcommand.equals("acceptinvite")) {
            //    return handleLeave(sender, org, args);
            } else {
                // No valid subcommand – Offer Guidance
                sender.sendMessage(ChatColor.RED + "Usage: /wde org <org_name> <create|info|dissolve|invitemember|...>");
                return true;
            }
        } else {
            // No subcommand (but we have org name): Default to display info?
            sender.sendMessage(ChatColor.RED + "Usage: /wde org <org_name> <create|info|dissolve|invitemember|...>");
            return true;
        }
        return true;
    }

    private boolean handleAddMember(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can add members to organizations.");
            return false;
        }
        Player player = (Player) sender;

        // Permission Check
        if (!org.getLeaderUUID().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of this organization.");
            return false;
        }

        // Target Player Retrieval (Ensure args length is at least 4)
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Please specify a player to add.");
            return false;
        }
        String targetPlayerName = args[3];

        UUID targetPlayerUUID = getPlayerUUIDFromName(targetPlayerName);
        if (targetPlayerUUID == null) {
            sender.sendMessage(ChatColor.RED + "Target player not found.");
            return false;
        }

        if (orgManager.isMember(org, targetPlayerUUID)) {
            sender.sendMessage(ChatColor.RED + "That player is already a member of the organization.");
            return false;
        }

        // Add the Member via OrgManager (with potential for other failure cases)
        try {
            orgManager.addMember(org, targetPlayerUUID);
            sender.sendMessage(ChatColor.GREEN + "Member added successfully.");
            return true;
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while adding the member.");
            return false;
        }
    }

    private boolean handleInviteMember(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can invite members.");
            return false;
        }

        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Please specify a player to invite.");
            return false;
        }
        String targetPlayerName = args[3];

        UUID targetPlayerUUID = getPlayerUUIDFromName(targetPlayerName);
        if (targetPlayerUUID == null) {
            sender.sendMessage(ChatColor.RED + "Target player not found.");
            return false;
        }

        // Add invite to 'pendingInvites'
        org.getPendingInvites().put(targetPlayerUUID, System.currentTimeMillis());

        sender.sendMessage(ChatColor.GREEN + "Invite sent to " + targetPlayerName);
        // Notify target player (You might want a more informative message with instructions)
        Bukkit.getPlayer(targetPlayerUUID).sendMessage(ChatColor.GREEN + "You've been invited!");
        return true;
    }

    private boolean handleAcceptInvite(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can accept invites.");
            return false;
        }
        Player player = (Player) sender;

        // Check if player has a pending invite  for this org
        if (!org.getPendingInvites().containsKey(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You do not have a pending invite for this organization.");
            return false;
        }

        // Check expiration (120 seconds in milliseconds)
        long inviteTimestamp = org.getPendingInvites().get(player.getUniqueId());
        if (System.currentTimeMillis() - inviteTimestamp > 120000) {
            org.getPendingInvites().remove(player.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Your invite has expired.");
            return false;
        }

        // Accept the invite (Add to members and remove from pending invites)
        orgManager.addMember(org, player.getUniqueId());
        org.getPendingInvites().remove(player.getUniqueId());

        sender.sendMessage(ChatColor.GREEN + "You have joined the organization.");
        return true;
    }

    private UUID getPlayerUUIDFromName(String playerName) {
        // Online check
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer != null) {
            return targetPlayer.getUniqueId();
        }

        // Using TownyUniverse (might require exception handling)
        try {
            Resident resident = TownyUniverse.getInstance().getResident(playerName);
            if (resident != null) {
                return resident.getUUID();
            }
        } catch (Exception e) {
            // Handle potential Towny issues as needed
        }

        return null;  // Player not found in any context
    }
    private boolean handleDissolve(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can dissolve organizations.");
            return true;
        }
        Player player = (Player) sender;

        if (!org.getLeaderUUID().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of this organization.");
            return true;
        }

        // Assuming orgManager.removeOrganization(org) exists
        orgManager.removeOrganization(org);
        orgManager.saveOrganizations();
        sender.sendMessage(ChatColor.GREEN + "Organization deleted.");
        return true;
    }

    private boolean handleLeave(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can leave organizations.");
            return false;
        }
        Player player = (Player) sender;

        // Check if the player is the leader
        if (org.getLeaderUUID().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Leaders cannot leave. Disband the organization using /wde org <org_name> dissolve");
            return false;
        }

        // Remove the member using OrgManager
        if (!orgManager.removeMember(org, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "An error occurred while leaving the organization.");
            return false;
        }

        sender.sendMessage(ChatColor.GREEN + "You have left the organization.");
        return true;
    }

    private boolean handleWithdraw(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can withdraw from organizations.");
            return false;
        }
        Player player = (Player) sender;

        // Permission Check
        if (!org.getLeaderUUID().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of this organization.");
            return false;
        }

        // Argument Parsing
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Please specify an amount to withdraw.");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid withdrawal amount.");
            return false;
        }

        // Balance Check
        if (amount > org.getBalance()) {
            sender.sendMessage(ChatColor.RED + "Insufficient funds in the organization.");
            return false;
        }

        // Economy Transaction (Using Vault)
        if (!economy.depositPlayer(player, amount).transactionSuccess()) {
            sender.sendMessage(ChatColor.RED + "An error occurred during the withdrawal.");
            return false;
        }

        // Update Organization Balance
        double newBalance = org.getBalance() - amount;
        orgManager.setBalance(org, newBalance);

        sender.sendMessage(ChatColor.GREEN + "Withdrawal successful.");
        return true;
    }


    private boolean handleKickMember(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can kick members.");
            return false;
        }
        Player player = (Player) sender;

        // Permission Check
        if (!org.getLeaderUUID().equals(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not the leader of this organization.");
            return false;
        }

        // Target Player Retrieval (Ensure args length is at least 4)
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Please specify a player to kick.");
            return false;
        }
        String targetPlayerName = args[3];

        // You might need to fetch Player or UUID via your Towny integration...
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Target player not found.");
            return false;
        }

        // Membership Validation
        if (!org.getMembers().contains(targetPlayer.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player is not a member of your organization.");
            return false;
        }

        // Removal via OrgManager
        if (!orgManager.removeMember(org, targetPlayer.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "An error occurred while removing the member.");
            return false;
        }

        sender.sendMessage(ChatColor.GREEN + "Member kicked successfully.");
        return true;
    }

    // Similarly for handleDeposit ...
    private boolean handleDeposit(CommandSender sender, WDEorg org, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can deposit to organizations.");
            return false;
        }
        Player player = (Player) sender;
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid deposit amount.");
            return false;
        }

        if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
            sender.sendMessage(ChatColor.RED + "You do not have enough funds.");
            return false;
        }

        // Update Organization Balance
        double newBalance = org.getBalance() + amount;
        orgManager.setBalance(org, newBalance);

        sender.sendMessage(ChatColor.GREEN + "Deposit successful.");
        return true;
    }

    private boolean displayOrgInfo(CommandSender sender, String orgName, WDEorg org) {
        if (org == null) {
            sender.sendMessage(ChatColor.RED + WorldDynamicsEngine.getInstance().getLocaleMessage(WorldDynamicsEngine.getInstance().userLang, "orgnotfound"));
            return true;
        }

        // Display organization information
        sender.sendMessage(ChatColor.GOLD + "=============[ " + ChatColor.GREEN + orgName + ChatColor.GOLD + " ]=============");
        sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.WHITE + getPlayerNameFromUUID(org.getLeaderUUID()));

        // Retrieve members as a Set<UUID> 
        Set<UUID> memberUUIDs = (Set<UUID>) org.getMembers();

        // Convert UUIDs to player names (assuming you can resolve those)
        List<String> memberNames = new ArrayList<>();
        for (UUID uuid : memberUUIDs) {
            String name = getPlayerNameFromUUID(uuid); // You'll need a UUID-to-name function.
            if (name != null) {
                memberNames.add(name);
            }
        }

        // Format the list of members as a String
        String membersList = String.join(", ", memberNames);
        sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + (membersList.isEmpty() ? "None" : membersList));

        sender.sendMessage(ChatColor.YELLOW + "Balance: " + ChatColor.WHITE + economy.format(org.getBalance()));

        sender.sendMessage(ChatColor.YELLOW + "Flags: " + ChatColor.WHITE + String.join(", ", org.getFlags()));

        // Add more fields related to your 'WDEorg' class as needed 

        return true;
    }

    private String getPlayerNameFromUUID(UUID uuid) {
        // 1. Attempt Online Lookup
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName(); // Player is online
        }

        // 2. Offline Representation
        return ChatColor.GRAY + "Offline User";
    }

    private boolean handleCreateOrg(CommandSender sender, WDEorg nation, String[] args, WorldDynamicsEngine plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create organizations.");
            return true;
        }

        Player player = (Player) sender;

        // Permission Check (using your "hasNationAuthority" function)
        if (!hasNationAuthority(player, nationManager)) {
            sender.sendMessage(ChatColor.RED + plugin.getLocaleMessage(plugin.userLang, "notallowed") );
            return true;
        }

        // Name Validation
        String orgName = args[1];  // Assuming organization name is at args[3]
       // if (!isValidOrgName(orgName)) {
       //     sender.sendMessage(ChatColor.RED + plugin.getLocaleMessage(plugin.userLang, "invalidname") );
       //     return true;
       // }

        // Check if an organization with that name already exists
        if (orgManager.getOrganization(orgName) != null) {
            sender.sendMessage(ChatColor.RED + plugin.getLocaleMessage(plugin.userLang, "alreadyexists"));
            return true;
        }

        // Vault Economy Check
        if (economy != null) {
            double cost = plugin.orgFormationCost;
            if (!economy.has(player, cost)) {
                sender.sendMessage(ChatColor.RED + plugin.getLocaleMessage(plugin.userLang, "cantpay"));
                return true;
            }
            economy.withdrawPlayer(player, cost);
        }

        // Create the organization
        WDEorg newOrg = new WDEorg(orgName, player.getUniqueId());
        orgManager.addOrganization(newOrg);
        orgManager.saveOrganizations();

        sender.sendMessage(ChatColor.GREEN + plugin.getLocaleMessage(plugin.userLang, "orgcreated"));
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

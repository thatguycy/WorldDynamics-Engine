package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTabCompleter implements TabCompleter {

    private OrgManager orgManager;

    public MyTabCompleter(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> onlinePlayerNames = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        if (cmd.getName().equalsIgnoreCase("wde")) {
            if (args.length == 1) {
                completions.add("help");
                completions.add("docs");
                completions.add("nation");
                completions.add("diplomacy");
                completions.add("org"); // Added 'org'
                // Add more first-level options as needed
            } else if (args.length == 2 && args[0].equalsIgnoreCase("nation")) {
                TownyUniverse.getInstance().getNations().forEach(nation -> completions.add(nation.getName()));
            } else if (args[0].equalsIgnoreCase("nation") && args.length == 3) {
                completions.addAll(Arrays.asList("setgovtype", "setgovleader", "addgovmember", "kickgovmember",
                        "appointarmycommander", "enlistarmymember", "dischargearmymember",
                        "adddiplomat", "removediplomat"));
            } else if (args[0].equalsIgnoreCase("nation") && args.length == 4) {
                switch (args[2].toLowerCase()) {
                    case "setgovtype":
                        completions.addAll(WorldDynamicsEngine.getInstance().getConfig().getStringList("validGovernmentTypes"));
                        break;
                    case "setgovleader":
                    case "addgovmember":
                    case "kickgovmember":
                    case "appointarmycommander":
                    case "enlistarmymember":
                    case "dischargearmymember":
                    case "adddiplomat":
                    case "removediplomat":
                        completions.addAll(onlinePlayerNames);
                        break;
                }
            } else if (args[0].equalsIgnoreCase("diplomacy") && args.length == 2) {
                completions.addAll(Arrays.asList("setrelation", "trading", "viewtrading", "relations", "visit"));
            } else if (args[0].equalsIgnoreCase("diplomacy") && args.length == 3) {
                if (args[1].equalsIgnoreCase("setrelation") || args[1].equalsIgnoreCase("trading")) {
                    TownyUniverse.getInstance().getNations().forEach(nation -> completions.add(nation.getName()));
                } else if (args[1].equalsIgnoreCase("visit")) {
                    TownyUniverse.getInstance().getNations().forEach(nation -> completions.add(nation.getName()));
                }
            } else if (args[0].equalsIgnoreCase("diplomacy") && args.length == 4) {
                if (args[1].equalsIgnoreCase("setrelation")) {
                    completions.addAll(Arrays.asList("friendly", "neutral", "unfriendly"));
                } else if (args[1].equalsIgnoreCase("trading")) {
                    completions.addAll(Arrays.asList("enabled", "disabled"));
                }
            }  else if (args.length == 3 && args[0].equalsIgnoreCase("org")) {
                completions.addAll(Arrays.asList("create", "dissolve", "addmember", "kickmember", "deposit", "withdraw", "leave", "info"));
            } else if (args.length == 2 && args[0].equalsIgnoreCase("org")) {
                // Fetch organization names dynamically from OrgManager
                try {
                    List<String> orgNames = orgManager.getAllOrganizationNames();
                    completions.addAll(orgNames);
                } catch (Exception e) {
                    // Log or handle any errors that might occur during retrieval
                }
            } else if (args.length == 4 && args[0].equalsIgnoreCase("org")) {
                if (args[2].equalsIgnoreCase("addmember") || args[2].equalsIgnoreCase("kickmember")) {
                    completions.addAll(onlinePlayerNames);
                }
            }
        }

        return completions;
    }

}
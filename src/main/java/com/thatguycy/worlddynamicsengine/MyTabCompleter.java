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

    @Override
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
                // Add more first-level options as needed
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("nation")) {
                TownyUniverse.getInstance().getNations().forEach(nation -> completions.add(nation.getName()));
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase("nation")) {
                completions.addAll(Arrays.asList("setgovtype", "setgovleader", "addgovmember", "kickgovmember", "appointarmycommander", "enlistarmymember", "dischargearmymember"));
            }
            else if (args.length == 4 && args[0].equalsIgnoreCase("nation")) {
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
                        completions.addAll(onlinePlayerNames);
                        break;
                }
            }
            // Handle other specific subcommands and arguments...
        }

        return completions;
    }

}

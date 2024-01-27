package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyUniverse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class MyTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Example: Command /wde <arg>
        if (cmd.getName().equalsIgnoreCase("wde")) {
            if (args.length == 1) {
                // First argument completion
                completions.add("help");
                completions.add("docs");
                completions.add("nation");
                // Add more options as needed
            } else if (args.length == 2 && args[0].equalsIgnoreCase("nation")) {
                List<String> nations = new ArrayList<>();
                TownyUniverse.getInstance().getNations().forEach(nation -> nations.add(nation.getName()));
                completions.addAll(nations);
            }
            // Handle other subcommands and arguments...
        }

        return completions;
    }
}

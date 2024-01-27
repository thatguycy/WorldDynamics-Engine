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

        if (cmd.getName().equalsIgnoreCase("wde")) {
            if (args.length == 1) {
                // First argument completion
                completions.add("help");
                completions.add("docs");
                completions.add("nation");
                // Add more options as needed
            }
            else if (args.length == 2 && args[0].equalsIgnoreCase("nation")) {
                // If only "nation" is typed, suggest nation names
                TownyUniverse.getInstance().getNations().forEach(nation -> completions.add(nation.getName()));
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase("nation")) {
                // If "/wde nation <name>" is typed, suggest "setgovtype"
                completions.add("setgovtype");
            }
            else if (args.length == 4 && args[0].equalsIgnoreCase("nation") && args[2].equalsIgnoreCase("setgovtype")) {
                // If "/wde nation <name> setgovtype" is typed, suggest government types
                completions.addAll(WorldDynamicsEngine.getInstance().getConfig().getStringList("validGovernmentTypes"));
            }
            // Handle other specific subcommands and arguments...
        }

        return completions;
    }
}

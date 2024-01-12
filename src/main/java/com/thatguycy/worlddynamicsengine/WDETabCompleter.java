package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WDETabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("wde")) {
            if (args.length == 1) {
                // First argument completion (subcommands)
                return getListOfStringsMatchingLastWord(args, "government", "help", "nation");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("government")) {
                // Second argument completion for /wde government
                return getListOfStringsMatchingLastWord(args, "settype", "info");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("nation")) {
                return getListOfNations();
            } else if (args.length == 3 && args[0].equalsIgnoreCase("government") && args[1].equalsIgnoreCase("settype")) {
                // Third argument completion for /wde government settype
                return getListOfGovernmentTypes(args);
            }
        }
        return null;
    }

    private List<String> getListOfStringsMatchingLastWord(String[] args, String... completions) {
        String lastWord = args[args.length - 1].toLowerCase();
        List<String> matches = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lastWord)) {
                matches.add(completion);
            }
        }
        return matches;
    }

    private List<String> getListOfGovernmentTypes(String[] args) {
        String lastWord = args[args.length - 1].toLowerCase();
        return Stream.of(GovernmentType.values())
                .map(Enum::name)
                .filter(name -> name.toLowerCase().startsWith(lastWord))
                .collect(Collectors.toList());
    }

    private List<String> getListOfNations() {
        return TownyUniverse.getInstance().getNations()
                .stream()
                .map(nation -> nation.getName())
                .collect(Collectors.toList());
    }
}

package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WDETabCompleter implements TabCompleter {
    private final OrganizationManager organizationManager;

    public WDETabCompleter(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("wde")) {
            if (args.length == 1) {
                // First argument completion (main subcommands)
                return getListOfStringsMatchingLastWord(args, "government", "help", "nation", "army", "org");
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "government":
                        return getListOfStringsMatchingLastWord(args, "settype", "info", "leave", "setleader", "addmember", "kickmember");
                    case "nation":
                        return getListOfNations();
                    case "army":
                        return getListOfStringsMatchingLastWord(args, "leave", "setleader", "addmember", "kickmember");
                    case "org":
                        return getListOfStringsMatchingLastWord(args, "create", "deposit", "withdraw", "join", "leave", "addmember", "kickmember", "info", "setattr");
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("government") && args[1].equalsIgnoreCase("settype")) {
                    return getListOfGovernmentTypes(args);
                } else if (args[0].equalsIgnoreCase("org")) {
                    switch (args[1].toLowerCase()) {
                        case "create":
                            return getListOfOrgTypes(); // Assuming this method returns a list of organization types
                        case "deposit":
                        case "withdraw":
                        case "join":
                        case "leave":
                        case "addmember":
                        case "kickmember":
                        case "info":
                            return getListOfOrgNames(); // Assuming this method returns a list of existing organization names
                        case "setattr":
                            if (args.length == 3) { // When typing the organization name
                                return getListOfOrgNames();
                            } else if (args.length == 4) { // When typing the attribute
                                return getListOfOrgAttributes();
                            }
                            break;
                    }
                }
            }
            // Additional logic for other argument lengths as needed
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
        return GovernmentType.getTypes().stream()
                .filter(name -> name.toLowerCase().startsWith(lastWord))
                .collect(Collectors.toList());
    }


    private List<String> getListOfNations() {
        return TownyUniverse.getInstance().getNations()
                .stream()
                .map(nation -> nation.getName())
                .collect(Collectors.toList());
    }

    private List<String> getListOfOrgTypes() {
        // Example: return a list of predefined organization types
        return Arrays.asList("BUSINESS", "GOVERNMENTAL", "INTERNATIONAL");
    }

    private List<String> getListOfOrgAttributes() {
        return Arrays.asList(
                "NONE",
                "BANK",
                "PASSPORT_OFFICE",
                "EMBASSY",
                "TRADE_CENTER",
                "CULTURAL_INSTITUTE",
                "MILITARY_BASE",
                "RESEARCH_LAB",
                "EDUCATIONAL_INSTITUTE",
                "MEDICAL_CENTER",
                "MARKETPLACE",
                "TRANSPORT_HUB",
                "LEGAL_COURT",
                "ENVIRONMENTAL_AGENCY",
                "HOUSING_COMPLEX",
                "AGRICULTURAL_FACILITY",
                "ENERGY_PLANT",
                "NEWS_AGENCY",
                "ENTERTAINMENT_VENUE",
                "TOURIST_ATTRACTION"
        );
    }

    private List<String> getListOfOrgNames() {
        return new ArrayList<>(organizationManager.getOrganizations().keySet());
    }
}

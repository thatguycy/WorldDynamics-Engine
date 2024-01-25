package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.thatguycy.worlddynamicsengine.WDEnation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestNationCommand implements CommandExecutor {
    private NationManager nationManager;

    public TestNationCommand(NationManager nationManager) {
        this.nationManager = nationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /wde test {NationName}");
            return true;
        }

        String nationName = args[1];
        if (nationManager.getNation(nationName) != null) {
            sender.sendMessage("Nation already registered in WDE.");
            return true;
        }

        com.palmergames.bukkit.towny.object.Nation townyNation = TownyUniverse.getInstance().getNation(nationName);
        if (townyNation == null) {
            sender.sendMessage("Nation not found in Towny.");
            return true;
        }

        // Create a new WDEnation object with default properties
        WDEnation newWDEnation = new WDEnation(nationName);

        nationManager.addNation(nationName, newWDEnation);
        sender.sendMessage("Nation '" + nationName + "' added to WDE.");
        return true;
    }
}
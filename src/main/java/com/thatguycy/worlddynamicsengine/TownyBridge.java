package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.thatguycy.worlddynamicsengine.UserUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TownyBridge {
    public static Town getPlayerTown(Player player) {
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if (resident.hasTown()) {
                return resident.getTown();
            }
        } catch (NotRegisteredException e) {
            // This exception is thrown if the player is not registered as a resident
            e.printStackTrace();
        }
        return null; // Return null if the player is not part of any town
    }
    public static Nation getPlayerNation(Player player) {
        try {
            return getPlayerTown(player).getNation();
        } catch (NotRegisteredException e) {
            // This exception is thrown if the player is not registered as a resident
            e.printStackTrace();
        }
        return null; // Return null if the player is not part of any town
    }
    public static Resident getResident(Player player){
        return TownyUniverse.getInstance().getResident(player.getName());
    }
    public static boolean isKing(Player player, Nation nation) {
        return nation.getKing() == getResident(player);
    }
    public static boolean isMayor(Player player, Town town) {
        return town.getMayor() == getResident(player);
    }
}

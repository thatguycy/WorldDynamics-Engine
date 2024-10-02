package org.nebulaone.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.nebulaone.data.DataManager;
import org.nebulaone.data.ResidentClass;

import java.util.Arrays;

public class ResidentCommand {

    private final DataManager dataManager = new DataManager(Bukkit.getServer().getPluginManager().getPlugin("WorldDynamicsEngine").getDataFolder());

    // [thatguycy] Executes the /wde resident command, opening the GUI for the player. ({1/10/2024}/{0.3.0})
    public void execute(Player player) {
        ResidentClass resident = dataManager.getResident(player.getUniqueId());

        if (resident != null) {
            // [thatguycy] Create a 9-slot inventory with a gradient title. ({1/10/2024}/{0.3.0})
            Inventory gui = Bukkit.createInventory(null, 9, "Resident UI");

            // [thatguycy] Create Username item using a Name Tag material. ({1/10/2024}/{0.3.0})
            ItemStack usernameItem = new ItemStack(Material.NAME_TAG);
            ItemMeta usernameMeta = usernameItem.getItemMeta();
            if (usernameMeta != null) {
                usernameMeta.setDisplayName(ChatColor.GOLD + "Username: " + ChatColor.WHITE + resident.getUsername());
                usernameMeta.setLore(Arrays.asList(ChatColor.GRAY + "This is your Minecraft username."));
                usernameItem.setItemMeta(usernameMeta);
            }

            // [thatguycy] Create Town item using a Paper material to represent the player's town. ({1/10/2024}/{0.3.0})
            ItemStack townItem = new ItemStack(Material.PAPER);
            ItemMeta townMeta = townItem.getItemMeta();
            if (townMeta != null) {
                townMeta.setDisplayName(ChatColor.AQUA + "Town: " + ChatColor.WHITE + resident.getTown());
                townMeta.setLore(Arrays.asList(ChatColor.GRAY + "This is your current Towny town."));
                townItem.setItemMeta(townMeta);
            }

            // [thatguycy] Add both items to the GUI in slots 0 and 1. ({1/10/2024}/{0.3.0})
            gui.setItem(0, usernameItem);
            gui.setItem(1, townItem);

            // [thatguycy] Open the GUI for the player. ({1/10/2024}/{0.3.0})
            player.openInventory(gui);
        } else {
            player.sendMessage(ChatColor.RED + "No resident data found.");
        }
    }
}

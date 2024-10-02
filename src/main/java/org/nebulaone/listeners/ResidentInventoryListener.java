package org.nebulaone.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

public class ResidentInventoryListener implements Listener {

    // [thatguycy] Prevent items from being dragged in/out of the resident GUI. ({1/10/2024}/{0.3.0})
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();

        // [thatguycy] Check if the inventory title matches the resident GUI. ({1/10/2024}/{0.3.0})
        if (view.getTitle().contains("Resident UI")) {
            event.setCancelled(true); // Prevent the click event (item movement)
        }
    }

    // [thatguycy] Prevent item dragging in the resident GUI. ({1/10/2024}/{0.3.0})
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();

        // [thatguycy] Check if the inventory title matches the resident GUI. ({1/10/2024}/{0.3.0})
        if (view.getTitle().contains("Resident UI")) {
            event.setCancelled(true); // Prevent item drag events
        }
    }
}

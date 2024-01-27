package com.thatguycy.worlddynamicsengine;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DocCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TextComponent message = new TextComponent("Click here for documentation.");
        message.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://docs.thatguycy.com/v/worlddynamics-engine/"));
        sender.spigot().sendMessage(message);

        return true;
    }
}
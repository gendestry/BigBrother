package org.eu.oberstar.bigbrother;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHome implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            Pos pos = DBInterface.getHome(player.getName());

            if(pos == null) {
                TextComponent msg = new TextComponent("Home not set!");
                msg.setColor(ChatColor.RED);
                player.spigot().sendMessage(msg);
                return true;
            }
            
            Location loc = new Location(Bukkit.getWorlds().get(0), pos.x, pos.y, pos.z);
            player.teleport(loc);

            TextComponent msg = new TextComponent("Teleported home!");
            msg.setColor(ChatColor.GRAY);
            player.spigot().sendMessage(msg);
            return true;
        }

        return false;
    }
    
}

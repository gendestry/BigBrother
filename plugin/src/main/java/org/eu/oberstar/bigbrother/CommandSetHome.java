package org.eu.oberstar.bigbrother;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSetHome implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            Location location = player.getLocation();
            float x = (float)location.getX();
            float y = (float)location.getY();
            float z = (float)location.getZ();

            x = Math.round(x * 100.f) / 100.f;
            y = Math.round(y * 100.f) / 100.f;
            z = Math.round(z * 100.f) / 100.f;

            DBInterface.setHome(player.getName(), x, y, z);
            TextComponent msg = new TextComponent("Home set successfully!");
            msg.setColor(ChatColor.GREEN);
            player.spigot().sendMessage(msg);
            return true;
        }

        return false;
    }
    
}

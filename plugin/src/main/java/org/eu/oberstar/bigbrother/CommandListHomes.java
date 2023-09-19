package org.eu.oberstar.bigbrother;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;


public class CommandListHomes implements CommandExecutor {
    
     @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            HomeLocations homes = DBInterface.listHomes();
            if(homes == null)
                return false;

            for(Pos pos : homes.homes) {
                TextComponent msg = new TextComponent(pos.username + " ");
                TextComponent coords = new TextComponent("[" + pos.x + ", " + pos.y + ", " + pos.z + "]");
                coords.setColor(ChatColor.AQUA);
                coords.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + pos.x + " " + pos.y + " " + pos.z));
                coords.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Teleport to " + pos.username + "'s home")));
                msg.addExtra(coords);
                player.spigot().sendMessage(msg);
            }

            return true;
        }
        
        return false;
    }
}

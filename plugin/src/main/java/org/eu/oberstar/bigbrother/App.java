package org.eu.oberstar.bigbrother;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class App extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("sethome").setExecutor(new CommandSetHome());
        getCommand("home").setExecutor(new CommandHome());
        getCommand("listhomes").setExecutor(new CommandListHomes());
    }

    @Override
    public void onDisable() {
        DBInterface.close();
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();
        User user = DBInterface.getUser(username);

        if(user == null) {
            player.kickPlayer("You are not registered on this server! Register on this website: https://oberstar.eu.org/minecraft");
            return;
        }

        if(!user.confirmed) {
            player.kickPlayer("Your account is not confirmed! Ask an admin to confirm your account.");
            return;
        }

        DBInterface.createSession(user.id, ip);
    }

    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        User user = DBInterface.getUser(username);

        if(user == null)
            return;

        DBInterface.concludeSession(user.id, event.getQuitMessage());
    }

    @EventHandler
	public void OnPreCommand(PlayerCommandPreprocessEvent event) {
        String username = event.getPlayer().getName();
        User user = DBInterface.getUser(username);

        if(user == null)
            return;
        
        DBInterface.logCommand(user.id, event.getMessage());
	}

}
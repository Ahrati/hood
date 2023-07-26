package fasttravel.discovery.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class FastTravelDiscoveryHandler implements Listener {
    public FastTravelDiscoveryHandler (Plugin plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

    private int x = -328, z = -22;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();

        if (playerX > x || playerX < x){
            return;
        }
        if (playerZ > z || playerZ < z){
            return;
        }

        player.sendMessage("You are within a zone!!! " + System.currentTimeMillis());
    }
}

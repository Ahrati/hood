package fasttravel.discovery.handlers;

import db.database;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import fasttravel.FastTravelPoint;
import fasttravel.FastTravelRepository;
import title.Title;

import java.sql.SQLException;

public class FastTravelDiscoveryHandler implements Listener {
    private final Plugin plugin;
    private final FastTravelRepository fastTravelRepository;

    public FastTravelDiscoveryHandler(Plugin plugin, database db) {
        this.plugin = plugin;
        this.fastTravelRepository = new FastTravelRepository(db);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();

        //player.sendMessage("current location: " + playerX + " " + playerZ);

        // Check if the player has any undiscovered fast travel points in this zone
        if (!FastTravelRepository.UndiscoveredFTP.containsKey(player.getUniqueId())) {
            return;
        }

        // Get the undiscovered fast travel points for this player
        for (FastTravelPoint fastTravelPoint : FastTravelRepository.UndiscoveredFTP.get(player.getUniqueId())) {
            int ftpX = fastTravelPoint.getX();
            int ftpZ = fastTravelPoint.getZ();
            int radius = 2 * fastTravelPoint.getRadius();
            //player.sendMessage(fastTravelPoint.getName());

            // Check if the player has discovered the fast travel point
            if (playerX < ftpX + radius && playerZ < ftpZ + radius && playerX > ftpX - radius && playerZ > ftpZ -radius) {
                // Set the fast travel point as discovered
                try {
                    fastTravelRepository.SetDiscovered(player.getUniqueId(), fastTravelPoint.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // Display the message
                //player.sendMessage("§aYou have discovered " + fastTravelPoint.getName());
                String titleMessage = "§aYou have discovered §6" + fastTravelPoint.getName();
                Title.sendTitle(player, titleMessage, 20, 70, 50);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        try {
            fastTravelRepository.PopulateFastTravelDiscovery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

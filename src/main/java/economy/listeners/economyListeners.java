package economy.listeners;

import db.database;
import economy.model.User;
import economy.repository.PlayerRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class economyListeners implements Listener {
    private final PlayerRepository playerRepository;
    public economyListeners(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            User user = playerRepository.getPlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not get joined player.");
        }

    }
}

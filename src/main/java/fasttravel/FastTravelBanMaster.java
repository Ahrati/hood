package fasttravel;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * This class manages the fast travel banning of players
 */
public class FastTravelBanMaster {
    private Plugin plugin;
    private final NamespacedKey bannedKey;

    public FastTravelBanMaster(Plugin plugin) {
        this.plugin = plugin;
        this.bannedKey = new NamespacedKey(plugin, "fast_travel_banned");
    }

    /**
     * Fast travel bans a player.
     */
    public void FastTravelBan(Player player) {
        if (player != null) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.set(bannedKey, PersistentDataType.BYTE, (byte) 1);
        }
    }

    /**
     * Fast travel unbans a player
     */
    public void FastTravelUnban(Player player) {
        if (player != null) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.remove(bannedKey);
        }
    }

    /**
     * Checks if a player is fast travel banned.
     */
    public boolean IsFastTravelBanned(Player player) {
        if (player != null) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            return dataContainer.has(bannedKey, PersistentDataType.BYTE);
        }
        return false;
    }
}

package fasttravel;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FastTravelBanMaster {
    Plugin plugin;
    FastTravelBanMaster(Plugin plugin){
        this.plugin = plugin;
    }

    public void FastTravelBan(Player player) {
        if (player != null) {
            player.addAttachment(plugin, "fasttravel.use", false);
        }
    }

    public void FastTravelUnban(Player player) {
        if (player != null) {
            player.addAttachment(plugin, "fasttravel.use", true);
        }
    }
}

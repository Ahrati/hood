import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HoodPlugin extends JavaPlugin {

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getLogger().log(Level.INFO, "Hood loaded.");
    }
}

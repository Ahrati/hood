import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;
import db.database;
public class HoodPlugin extends JavaPlugin {
    FileConfiguration config = getConfig();
    private database db;
    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        // CONFIG
        config.addDefault("ip address", "localhost");
        config.addDefault("port", "3306");
        config.addDefault("database", "database");
        config.addDefault("user", "root");
        config.addDefault("password", "");
        config.options().copyDefaults(true);
        saveConfig();

        // DB
        db = new database();
        try {
            this.db.initializeDatabase(config);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize database.");
        }

        // ECONOMY
        try {
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS player (player_uuid CHAR(36) PRIMARY KEY, username VARCHAR(255), money INT);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize economy tables.");
        }

        // FASTTRAVEL
        try {
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS fasttravelpoints (name VARCHAR(255) PRIMARY KEY, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, radius INT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize fast travel table.");
        }

        // LOADED
        super.onEnable();
        this.getLogger().log(Level.INFO, "Hood loaded.");
    }
}

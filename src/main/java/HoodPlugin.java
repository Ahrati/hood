import fasttravel.FastTravelCommand;
import fasttravel.FastTravelListCommand;
import fasttravel.FastTravelPointDeleteCommand;
import fasttravel.FastTravelPointSetCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import economy.listeners.economyListeners;
import economy.repository.PlayerRepository;
import fasttravel.FastTravelPointSetCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import db.database;
import economy.*;
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

        // FAST TRAVEL
        PlayerRepository prepo = new PlayerRepository(db);
        Objects.requireNonNull(getCommand("bal")).setExecutor(new balCommand(prepo));
        Objects.requireNonNull(getCommand("bal")).setTabCompleter(new balCommand(prepo));

        Objects.requireNonNull(getCommand("pay")).setExecutor(new payCommand(prepo));
        Objects.requireNonNull(getCommand("pay")).setTabCompleter(new payCommand(prepo));

        Objects.requireNonNull(getCommand("balop")).setExecutor(new balopCommand(prepo));
        Objects.requireNonNull(getCommand("balop")).setTabCompleter(new balopCommand(prepo));

        getServer().getPluginManager().registerEvents(new economyListeners(prepo), this);

        // FAST TRAVEL
        try {
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS fasttravelpoints (name VARCHAR(255) PRIMARY KEY, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, radius INT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize fast travel table.");
        }

        getCommand("fasttravelpointset").setExecutor(new FastTravelPointSetCommand(db));
        getCommand("fasttravelpointset").setPermission("myplugin.admin");


        getCommand("fasttravellist").setExecutor(new FastTravelListCommand(db));

        Objects.requireNonNull(getCommand("fasttravel")).setExecutor(new FastTravelCommand(db, this));
        Objects.requireNonNull(getCommand("fasttravel")).setTabCompleter(new FastTravelCommand(db, this));

        Objects.requireNonNull(getCommand("fasttravelpointdelete")).setExecutor(new FastTravelPointDeleteCommand(db));
        Objects.requireNonNull(getCommand("fasttravelpointdelete")).setTabCompleter(new FastTravelPointDeleteCommand(db));
        getCommand("fasttravelpointdelete").setPermission("myplugin.admin");

        // LOADED
        super.onEnable();
        this.getLogger().log(Level.INFO, "Hood loaded.");
    }
}

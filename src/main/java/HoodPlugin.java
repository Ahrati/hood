import economy.handler.MoneyHandler;
import economy.repository.OrganisationRepository;
import fasttravel.*;
import jail.*;
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
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS user (player_uuid CHAR(36) PRIMARY KEY, username VARCHAR(255), money INT);");
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS organisation (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(255) NOT NULL, description VARCHAR(255), memberlistid INT, money INT, FOREIGN KEY (memberlistid) REFERENCES memberlist(id));");
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS memberlist (uuid CHAR(36), organisationid INT, FOREIGN KEY (uuid) REFERENCES user(player_uuid), FOREIGN KEY (organisationid) REFERENCES organisation(id));");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize economy tables.");
        }

        PlayerRepository prepo = new PlayerRepository(db);
        OrganisationRepository orepo = new OrganisationRepository();
        MoneyHandler moneyHandler = new MoneyHandler(prepo, orepo);

        Objects.requireNonNull(getCommand("bal")).setExecutor(new balCommand(moneyHandler));
        Objects.requireNonNull(getCommand("bal")).setTabCompleter(new balCommand(moneyHandler));

        Objects.requireNonNull(getCommand("pay")).setExecutor(new payCommand(moneyHandler));
        Objects.requireNonNull(getCommand("pay")).setTabCompleter(new payCommand(moneyHandler));

        Objects.requireNonNull(getCommand("balop")).setExecutor(new balopCommand(moneyHandler));
        Objects.requireNonNull(getCommand("balop")).setTabCompleter(new balopCommand(moneyHandler));
        Objects.requireNonNull(getCommand("balop")).setPermission("myplugin.admin");

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

        getCommand("fasttravelban").setExecutor(new FastTravelBanCommand(this));
        getCommand("fasttravelban").setPermission("myplugin.admin");

        getCommand("fasttravelunban").setExecutor(new FastTravelUnbanCommand(this));
        getCommand("fasttravelunban").setPermission("myplugin.admin");

        //JAIL
        try {
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS jails (name VARCHAR(255) PRIMARY KEY, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize jails table.");
        }

        getCommand("jailset").setExecutor(new JailSetCommand(db));
        getCommand("jailset").setPermission("myplugin.admin");

        getCommand("jail").setExecutor(new JailCommand(db));
        getCommand("jail").setPermission("myplugin.admin");
        getCommand("jail").setTabCompleter(new JailCommand(db));

        getCommand("jailfree").setExecutor(new JailFreeCommand());
        getCommand("jailfree").setPermission("myplugin.admin");

        getCommand("jaillist").setExecutor(new JailListCommand(db));
        getCommand("jaillist").setPermission("myplugin.admin");

        getCommand("jaildelete").setExecutor(new JailDeleteCommand(db));
        getCommand("jaildelete").setPermission("myplugin.admin");
        getCommand("jaildelete").setTabCompleter(new JailDeleteCommand(db));

        // LOADED
        super.onEnable();
        this.getLogger().log(Level.INFO, "Hood loaded.");
    }
}

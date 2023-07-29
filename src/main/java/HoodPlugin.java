import casinochips.CasinoChipCraftListener;
import casinochips.CasinoChipsItemManager;
import economy.handler.MoneyHandler;
import economy.handler.OrganisationHandler;
import economy.repository.OrganisationRepository;
import economy.repository.TransactionLogRepository;
import fasttravel.*;
import fasttravel.discovery.handlers.FastTravelDiscoveryHandler;
import jail.*;
import economy.listeners.economyListeners;
import economy.repository.PlayerRepository;
import fasttravel.FastTravelPointSetCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import db.database;
import economy.*;
import transactionsign.TransactionSignListener;

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
        config.addDefault("maxOrg", 5);
        config.addDefault("maxOrgMembers", 30);
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
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS organisation (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, description VARCHAR(255), money INT);");
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS memberlist (uuid CHAR(36), organisationid INT, role VARCHAR(255), FOREIGN KEY (uuid) REFERENCES user(player_uuid), FOREIGN KEY (organisationid) REFERENCES organisation(id));");
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS transactionlog (transaction_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, mode VARCHAR(255) NOT NULL, sender VARCHAR(255) NOT NULL, receiver VARCHAR(255) NOT NULL, amount INT NOT NULL, transaction_description VARCHAR(255), date DATETIME NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize economy tables.");
        }

        PlayerRepository prepo = new PlayerRepository(db);
        OrganisationRepository orepo = new OrganisationRepository(db);
        TransactionLogRepository trepo = new TransactionLogRepository(db);

        MoneyHandler moneyHandler = new MoneyHandler(prepo, orepo, trepo, this);
        OrganisationHandler organisationHandler = new OrganisationHandler(prepo, orepo, config, moneyHandler);

        Objects.requireNonNull(getCommand("bal")).setExecutor(new balCommand(moneyHandler));
        Objects.requireNonNull(getCommand("bal")).setTabCompleter(new balCommand(moneyHandler));

        Objects.requireNonNull(getCommand("pay")).setExecutor(new payCommand(moneyHandler));
        Objects.requireNonNull(getCommand("pay")).setTabCompleter(new payCommand(moneyHandler));

        Objects.requireNonNull(getCommand("balop")).setExecutor(new balopCommand(moneyHandler, organisationHandler));
        Objects.requireNonNull(getCommand("balop")).setTabCompleter(new balopCommand(moneyHandler, organisationHandler));
        Objects.requireNonNull(getCommand("balop")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("org")).setExecutor(new orgCommand(moneyHandler, organisationHandler));
        Objects.requireNonNull(getCommand("org")).setTabCompleter(new orgCommand(moneyHandler, organisationHandler));

        getServer().getPluginManager().registerEvents(new economyListeners(prepo), this);

        // FAST TRAVEL
        try {
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS fasttravelpoints (name VARCHAR(255) PRIMARY KEY, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, radius INT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize fast travel table.");
        }

        try {
            // Create the fasttraveldiscovery table if it doesn't exist
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS fasttraveldiscovery (ftpname VARCHAR(255) NOT NULL, playerid CHAR(36) NOT NULL, discovered BOOLEAN NOT NULL DEFAULT FALSE, PRIMARY KEY (ftpname, playerid), FOREIGN KEY (ftpname) REFERENCES fasttravelpoints(name) ON DELETE CASCADE, FOREIGN KEY (playerid) REFERENCES user (player_uuid) ON DELETE CASCADE);");

            // Insert rows into fasttraveldiscovery based on fasttravelpoints and user tables
            this.db.initializeTable("INSERT IGNORE INTO fasttraveldiscovery (ftpname, playerid)\n" +
                    "SELECT ftp.name, u.player_uuid\n" +
                    "FROM fasttravelpoints AS ftp\n" +
                    "CROSS JOIN user AS u;");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize fasttraveldiscovery table or insert rows.");
        }


        Objects.requireNonNull(getCommand("fasttravelpointset")).setExecutor(new FastTravelPointSetCommand(db));
        Objects.requireNonNull(getCommand("fasttravelpointset")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("fasttravellist")).setExecutor(new FastTravelListCommand(db));

        Objects.requireNonNull(getCommand("fasttravel")).setExecutor(new FastTravelCommand(db, this));
        Objects.requireNonNull(getCommand("fasttravel")).setTabCompleter(new FastTravelCommand(db, this));

        Objects.requireNonNull(getCommand("fasttravelpointdelete")).setExecutor(new FastTravelPointDeleteCommand(db));
        Objects.requireNonNull(getCommand("fasttravelpointdelete")).setTabCompleter(new FastTravelPointDeleteCommand(db));
        Objects.requireNonNull(getCommand("fasttravelpointdelete")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("fasttravelban")).setExecutor(new FastTravelBanCommand(this));
        Objects.requireNonNull(getCommand("fasttravelban")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("fasttravelunban")).setExecutor(new FastTravelUnbanCommand(this));
        Objects.requireNonNull(getCommand("fasttravelunban")).setPermission("myplugin.admin");

        new FastTravelDiscoveryHandler(this,db);

        //JAIL
        try {
            this.db.initializeTable("CREATE TABLE IF NOT EXISTS jails (name VARCHAR(255) PRIMARY KEY, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize jails table.");
        }

        Objects.requireNonNull(getCommand("jailset")).setExecutor(new JailSetCommand(db));
        Objects.requireNonNull(getCommand("jailset")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("jail")).setExecutor(new JailCommand(db,this));
        Objects.requireNonNull(getCommand("jail")).setPermission("myplugin.admin");
        Objects.requireNonNull(getCommand("jail")).setTabCompleter(new JailCommand(db,this));

        Objects.requireNonNull(getCommand("jailfree")).setExecutor(new JailFreeCommand(this));
        Objects.requireNonNull(getCommand("jailfree")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("jaillist")).setExecutor(new JailListCommand(db));
        Objects.requireNonNull(getCommand("jaillist")).setPermission("myplugin.admin");

        Objects.requireNonNull(getCommand("jaildelete")).setExecutor(new JailDeleteCommand(db));
        Objects.requireNonNull(getCommand("jaildelete")).setPermission("myplugin.admin");
        Objects.requireNonNull(getCommand("jaildelete")).setTabCompleter(new JailDeleteCommand(db));

        //CASINO CHIPS
        CasinoChipsItemManager casinoChipsItemManager = new CasinoChipsItemManager(this);
        CasinoChipCraftListener casinoChipCraftListener = new CasinoChipCraftListener(this);

        //TRANSACTION SIGN
        TransactionSignListener transactionSignListener = new TransactionSignListener(this, organisationHandler);

        // LOADED
        super.onEnable();
        this.getLogger().log(Level.INFO, "Hood loaded.");
    }
}

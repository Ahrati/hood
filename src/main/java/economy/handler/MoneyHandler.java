package economy.handler;

import economy.model.Organisation;
import economy.model.TransactionLog;
import economy.model.User;
import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import economy.repository.TransactionLogRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getServer;
import static title.Title.sendActionBar;

public class MoneyHandler {
    private final PlayerRepository prepo;
    private final OrganisationRepository orepo;
    private final TransactionLogRepository logger;
    private Plugin plugin;
    private final NamespacedKey balviewKey;

    public MoneyHandler(PlayerRepository prepo, OrganisationRepository orepo, TransactionLogRepository trepo, Plugin plugin) {
        this.prepo = prepo;
        this.orepo = orepo;
        this.logger = trepo;
        this.plugin = plugin;
        this.balviewKey = new NamespacedKey(plugin, "balance_view");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::showActionBar, 1, 1, TimeUnit.SECONDS);

    }
    public List<TransactionLog> getHistory(String name, String mode) throws SQLException {
        List<TransactionLog> sorted = logger.getTransactions(name, mode);
        sorted.sort(Comparator.comparingLong(t -> t.getDatetime().getTime()));
        return sorted;
    }
    public void flipActionBar(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if(dataContainer.has(balviewKey, PersistentDataType.BYTE)) {
            dataContainer.remove(balviewKey);
        }else {
            dataContainer.set(balviewKey, PersistentDataType.BYTE, (byte) 1);
        }
    }
    public void showActionBar() {
        for(Player player : getServer().getOnlinePlayers()) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            if(dataContainer.has(balviewKey, PersistentDataType.BYTE)) {
                sendActionBar(player, "BALVIEW");
            }
        }
    }

    public int getBalance(Player sender) throws SQLException {
        return prepo.getPlayer((Player) sender).getMoney();
    }

    public List<User> getBalances() throws SQLException {
        return prepo.fetchPlayers();
    }

    public int getBalance(String from, String mode) throws SQLException {
        if(Objects.equals(mode, "p")) {
            return prepo.fetchPlayer(from).getMoney();
        } else if(Objects.equals(mode, "o")) {
            return orepo.fetchOrganisation(from).getMoney();
        } else {
            return -1;
        }
    }

    public void setBalance(String to, int amount, String mode) throws SQLException {
        if(Objects.equals(mode, "p")) {
            User user = prepo.fetchPlayer(to);
            user.setMoney(amount);
            prepo.updatePlayer(user);
        } else if(Objects.equals(mode, "o")) {
            Organisation org = orepo.fetchOrganisation(to);
            org.setMoney(amount);
            orepo.updateOrganisation(org);
        }
    }

    /*
        Transfer money between players and organisations.
        Returns an integer for error code:
        -1 - invalid mode
        0  - OK
        1  - Attempted transfer to self
        2  - Receiver not found
        3  - Sender not found
        4  - Not enough funds for transaction
        --- will add more if needed
        Requires string for mode to transfer in:
        p2p - player to player
        p2o - player to organisation
        o2p - organisation to player
        o2o - organisation to organisation
     */
    public int transferMoney(String from, String to, int amount, String description, String mode) throws SQLException {
        if(Objects.equals(mode, "p2p")) {
            User userFrom = prepo.fetchPlayer(from);

            Player sender = getServer().getPlayer(from);
            Player receiver = getServer().getPlayer(to);

            if (sender == null) {
                System.out.println("MASSIVE TRANSFER FUNDS ERROR");
                return 3;
            }

            if(from.equalsIgnoreCase(to)) {
                sender.sendMessage("[§dEconomy§r] §cCannot transfer funds to yourself!");
                return 1;
            }

            if (receiver == null) {
                sender.sendMessage("[§dEconomy§r] §rPlayer not online!");
                return 2;
            }
            User userTo = prepo.fetchPlayer(to);

            int maxAmount;
            maxAmount = userFrom.getMoney();
            if(amount > maxAmount) {
                sender.sendMessage("[§dEconomy§r] §cYou dont have enough funds");
                return 4;
            }

            userFrom.setMoney(userFrom.getMoney() - amount);
            userTo.setMoney(userTo.getMoney() + amount);

            prepo.updatePlayer(userFrom);
            prepo.updatePlayer(userTo);

            logger.createTransaction(from, to, amount, description, mode);
            return 0;
        } else if(Objects.equals(mode, "p2o")) {
            User userFrom = prepo.fetchPlayer(from);

            Player sender = getServer().getPlayer(from);
            Organisation receiver = orepo.fetchOrganisation(to);

            if (sender == null) {
                System.out.println("MASSIVE TRANSFER FUNDS ERROR");
                return 3;
            }

            if (receiver == null) {
                sender.sendMessage("[§dEconomy§r] §rOrganisation not found!");
                return 2;
            }

            int maxAmount;
            maxAmount = userFrom.getMoney();
            if(amount > maxAmount) {
                sender.sendMessage("[§dEconomy§r] §cYou dont have enough funds");
                return 4;
            }

            userFrom.setMoney(userFrom.getMoney() - amount);
            receiver.setMoney(receiver.getMoney() + amount);

            prepo.updatePlayer(userFrom);
            orepo.updateOrganisation(receiver);

            logger.createTransaction(from, to, amount, description, mode);
            return 0;
        } else if(Objects.equals(mode, "o2p")) {
            Organisation sender = orepo.fetchOrganisation(from);
            Player receiver = getServer().getPlayer(to);

            if (sender == null) {
                System.out.println("MASSIVE TRANSFER FUNDS ERROR");
                return 3;
            }

            if (receiver == null) {
                return 2;
            }
            User userTo = prepo.fetchPlayer(to);

            int maxAmount;
            maxAmount = sender.getMoney();
            if(amount > maxAmount) {
                return 4;
            }

            sender.setMoney(sender.getMoney() - amount);
            userTo.setMoney(userTo.getMoney() + amount);

            orepo.updateOrganisation(sender);
            prepo.updatePlayer(userTo);

            logger.createTransaction(from, to, amount, description, mode);
            return 0;
        } else if(Objects.equals(mode, "o2o")) {
            Organisation sender = orepo.fetchOrganisation(from);
            Organisation receiver = orepo.fetchOrganisation(to);

            if (sender == null) {
                System.out.println("MASSIVE TRANSFER FUNDS ERROR");
                return 3;
            }

            if(from.equalsIgnoreCase(to)) {
                return 1;
            }

            if (receiver == null) {
                return 2;
            }

            int maxAmount;
            maxAmount = sender.getMoney();
            if(amount > maxAmount) {
                return 4;
            }

            sender.setMoney(sender.getMoney() - amount);
            receiver.setMoney(receiver.getMoney() + amount);

            orepo.updateOrganisation(sender);
            orepo.updateOrganisation(receiver);

            logger.createTransaction(from, to, amount, description, mode);
            return 0;
        }
        return -1;
    }
}

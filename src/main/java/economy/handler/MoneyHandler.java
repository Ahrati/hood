package economy.handler;

import economy.model.Organisation;
import economy.model.User;
import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class MoneyHandler {
    private final PlayerRepository prepo;
    private final OrganisationRepository orepo;

    public MoneyHandler(PlayerRepository prepo, OrganisationRepository orepo) {
        this.prepo = prepo;
        this.orepo = orepo;
    }

    public int getBalance(Player sender) throws SQLException {
        return prepo.getPlayer((Player) sender).getMoney();
    }

    public int getBalance(String from, String mode) throws SQLException {
        if(Objects.equals(mode, "p")) {
            return prepo.fetchPlayer(from).getMoney();
        } else if(Objects.equals(mode, "o")) {
            return 0;
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
        }
    }

    /*
        Transfer money between players and organisations.
        Returns and integer for error code:
        -1 - invalid mode
        0  - OK
        1  - Attempted transfer to self
        2  - Receiver not found
        3  - Sender not found
        4  - Not enough funds for transaction
        --- will add more if needed
     */
    public int transferMoney(String from, String to, int amount, String mode) throws SQLException {
        if(Objects.equals(mode, "p2p")) {
            User userFrom = prepo.fetchPlayer(from);

            Player sender = getServer().getPlayer(from);
            Player receiver = getServer().getPlayer(to);

            if (sender == null) {
                System.out.println("MASSIVE TRANSFER FUNDS ERROR");
                return 3;
            }

            if(from.equalsIgnoreCase(to)) {
                sender.sendMessage("Cannot transfer funds to yourself!");
                return 1;
            }

            if (receiver == null) {
                sender.sendMessage("Player not online!");
                return 2;
            }
            User userTo = prepo.fetchPlayer(to);

            int maxAmount;
            maxAmount = userFrom.getMoney();
            if(amount > maxAmount) {
                sender.sendMessage("You dont have enough funds");
                return 4;
            }

            userFrom.setMoney(userFrom.getMoney() - amount);
            userTo.setMoney(userTo.getMoney() + amount);

            prepo.updatePlayer(userFrom);
            prepo.updatePlayer(userTo);

            return 0;
        } else if(Objects.equals(mode, "p2o")) {
            User userFrom = prepo.fetchPlayer(from);

            Player sender = getServer().getPlayer(from);
            Organisation receiver = orepo.fetchOrganisation(to);

            if (sender == null) {
                System.out.println("MASSIVE TRANSFER FUNDS ERROR");
                return 3;
            }

            if(from.equalsIgnoreCase(to)) {
                sender.sendMessage("Cannot transfer funds to yourself!");
                return 1;
            }

            if (receiver == null) {
                sender.sendMessage("Player not online!");
                return 2;
            }

            int maxAmount;
            maxAmount = userFrom.getMoney();
            if(amount > maxAmount) {
                sender.sendMessage("You dont have enough funds");
                return 4;
            }

            userFrom.setMoney(userFrom.getMoney() - amount);
            receiver.setMoney(receiver.getMoney() + amount);

            prepo.updatePlayer(userFrom);
            orepo.updateOrganisation(receiver);

            return 0;
        } else if(Objects.equals(mode, "o2p")) {
            Organisation sender = orepo.fetchOrganisation(from);
            Player receiver = getServer().getPlayer(to);

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

            return 0;
        }
        return -1;
    }
}

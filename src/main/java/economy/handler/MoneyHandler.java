package economy.handler;

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

    public boolean transferMoney(Player sender, String to, int amount, String mode) throws SQLException {
        if(Objects.equals(mode, "p2p")) {
            String from = sender.getName();
            User userFrom = prepo.fetchPlayer(from);

            Player receiver = getServer().getPlayer(to);

            if(from.equalsIgnoreCase(to)) {
                sender.sendMessage("Cannot transfer funds to yourself!");
                return false;
            }

            if (receiver == null) {
                sender.sendMessage("Player not online!");
                return false;
            }
            User userTo = prepo.fetchPlayer(to);

            int maxAmount;
            maxAmount = userFrom.getMoney();
            if(amount > maxAmount) {
                sender.sendMessage("You dont have enough funds");
                return false;
            }

            userFrom.setMoney(userFrom.getMoney() - amount);
            userTo.setMoney(userTo.getMoney() + amount);

            prepo.updatePlayer(userFrom);
            prepo.updatePlayer(userTo);

            return true;
        } else if(Objects.equals(mode, "p2o")) {
            return false;
        }
    }
}
